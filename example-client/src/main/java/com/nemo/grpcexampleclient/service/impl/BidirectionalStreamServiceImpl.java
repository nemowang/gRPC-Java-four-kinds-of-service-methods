package com.nemo.grpcexampleclient.service.impl;

import com.nemo.grpcexampleclient.service.BidirectionalStreamService;
import com.nemo.grpcexampleserver.BidirectionalStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/22
 */
@Slf4j
@Service
public class BidirectionalStreamServiceImpl implements BidirectionalStreamService {

    @GrpcClient("server-service")
    BidirectionalStreamServiceGrpc.BidirectionalStreamServiceStub serviceStub;

    /**
     * 双向流式传输 - 字符串
     * @return
     */
    @Override
    @SneakyThrows
    public String bidirectionalStreamString() {
        StringBuilder resultBuilder = new StringBuilder();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<StringRequest> observer = serviceStub.bidirectionalStreamString(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("BidirectionalStreamServiceImpl bidirectionalStreamString onNext");
                log.info(stringResponse.getValue());
                // 分批次接收从服务端传来的数据
                resultBuilder.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("BidirectionalStreamServiceImpl bidirectionalStreamString onError {}.", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("BidirectionalStreamServiceImpl bidirectionalStreamString onCompleted.");
                countDownLatch.countDown();
            }
        });

        StringRequest.Builder builder = StringRequest.newBuilder();
        List<String> names = Arrays.asList("老番茄", "中国boy", "某幻君", "花少北", "LexBurner");
        // 分批次向服务端传送数据
        for (String name : names) {
            builder.setValue(name);
            observer.onNext(builder.build());
        }
        observer.onCompleted();
        countDownLatch.await();
        return resultBuilder.toString();
    }
}
