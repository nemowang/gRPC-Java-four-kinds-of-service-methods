package com.nemo.grpcexampleclient.service.impl;

import com.nemo.grpcexampleclient.service.ClientSideStreamService;
import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/20
 */
@Slf4j
@Service
public class ClientSideStreamServiceImpl implements ClientSideStreamService {

    // 客户端流式传输使用非阻塞服务
    @GrpcClient("server-service")
    private ClientSideStreamServiceGrpc.ClientSideStreamServiceStub serviceStub;

    /**
     * 客户端流式传输 - 字符串
     * @return
     */
    @SneakyThrows
    @Override
    public String clientStreamString() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StringBuilder result = new StringBuilder();

        StreamObserver<StringRequest> observer = serviceStub.clientStreamString(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("ClientSideStreamServiceImpl clientStreamString onNext stringResponse:{}", stringResponse);
                result.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("ClientSideStreamServiceImpl clientStreamString onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                log.info("ClientSideStreamServiceImpl clientStreamString onNext onCompleted");
            }
        });

        // 分批次向服务端发送数据
        for (int i = 0; i < 10; i++) {
            StringRequest.Builder builder = StringRequest.newBuilder();
            builder.setValue("客户端流式传输 - 字符串 第" + i + "次发送数据" + System.lineSeparator());
            observer.onNext(builder.build());
        }
        observer.onCompleted();
        countDownLatch.await();

        return result.toString();
    }
}
