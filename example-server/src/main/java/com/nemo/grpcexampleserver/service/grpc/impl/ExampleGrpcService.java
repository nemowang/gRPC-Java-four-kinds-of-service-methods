package com.nemo.grpcexampleserver.service.grpc.impl;

import com.nemo.grpcexampleserver.ExampleServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/11
 */
@Slf4j
@GrpcService
public class ExampleGrpcService extends ExampleServiceGrpc.ExampleServiceImplBase {

    @Override
    public void sayHelloWorld(StringRequest request, StreamObserver<StringResponse> responseObserver) {
        String hello = request.getValue() + " says hello world";
        responseObserver.onNext(StringResponse.newBuilder().setValue(hello).build());
        responseObserver.onCompleted();
    }
}
