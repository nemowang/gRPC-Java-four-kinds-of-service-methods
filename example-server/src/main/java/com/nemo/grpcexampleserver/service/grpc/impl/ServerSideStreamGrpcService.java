package com.nemo.grpcexampleserver.service.grpc.impl;

import com.nemo.grpcexampleserver.BytesResponse;
import com.nemo.grpcexampleserver.Empty;
import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/17
 */
@Slf4j
@GrpcService
public class ServerSideStreamGrpcService extends ServerSideStreamServiceGrpc.ServerSideStreamServiceImplBase {

    @Override
    public void serverStreamString(Empty request, StreamObserver<StringResponse> responseObserver) {
        StringResponse.Builder builder = StringResponse.newBuilder();
        // 通过流分7次向客户端发送数据
        for (int i = 0; i < 7; i++) {
            String message = "第" + i + "次发送数据 ";
            responseObserver.onNext(builder.setValue(message).build());
            log.info(message);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void serverStreamBytes(Empty request, StreamObserver<BytesResponse> responseObserver) {
        // TODO
        super.serverStreamBytes(request, responseObserver);
    }
}
