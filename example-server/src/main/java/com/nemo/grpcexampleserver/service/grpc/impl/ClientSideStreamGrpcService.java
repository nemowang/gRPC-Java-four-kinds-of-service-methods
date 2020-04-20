package com.nemo.grpcexampleserver.service.grpc.impl;

import com.nemo.grpcexampleserver.BytesRequest;
import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/20
 */
@Slf4j
@GrpcService
public class ClientSideStreamGrpcService extends ClientSideStreamServiceGrpc.ClientSideStreamServiceImplBase {

    /**
     * 客户端流式传输 - 字符串
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<StringRequest> clientStreamString(StreamObserver<StringResponse> responseObserver) {
        StringBuilder stringBuilder = new StringBuilder();

        return new StreamObserver<StringRequest>() {
            @Override
            public void onNext(StringRequest request) {
                log.info("clientStreamString onNext request={}", request.getValue());
                stringBuilder.append(request.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("clientStreamString onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                String requestString = stringBuilder.toString();
                StringResponse result = StringResponse.newBuilder().setValue("clientStreamString请求参数: " + requestString).build();
                responseObserver.onNext(result);
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * 客户端流式传输 - bytes 返回上传文件的保存路径
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<BytesRequest> clientStreamBytes(StreamObserver<StringResponse> responseObserver) {
        return super.clientStreamBytes(responseObserver);
    }
}
