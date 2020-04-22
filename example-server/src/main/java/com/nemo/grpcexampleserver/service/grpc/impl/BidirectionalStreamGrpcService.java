package com.nemo.grpcexampleserver.service.grpc.impl;

import com.nemo.grpcexampleserver.BidirectionalStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/22
 */
@Slf4j
@GrpcService
public class BidirectionalStreamGrpcService extends BidirectionalStreamServiceGrpc.BidirectionalStreamServiceImplBase {

    /**
     * 双向流式传输 - 字符串
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<StringRequest> bidirectionalStreamString(StreamObserver<StringResponse> responseObserver) {
        List<String> resultList = new ArrayList<>();

        return new StreamObserver<StringRequest>() {
            @Override
            public void onNext(StringRequest request) {
                log.info("BidirectionalStreamGrpcService bidirectionalStreamString onNext.");
                // 分批次接收客户端传来的数据并处理
                String name = request.getValue();
                String onNext = "欢迎" + name + "进入直播间." + System.lineSeparator();
                log.info(onNext);
                resultList.add(onNext);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("BidirectionalStreamGrpcService bidirectionalStreamString onError {}.", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("BidirectionalStreamGrpcService bidirectionalStreamString onCompleted.");
                StringResponse.Builder builder = StringResponse.newBuilder();
                // 分批次向客户端传送数据
                for (String s : resultList) {
                    responseObserver.onNext(builder.setValue(s).build());
                }
                responseObserver.onCompleted();
            }
        };
    }
}
