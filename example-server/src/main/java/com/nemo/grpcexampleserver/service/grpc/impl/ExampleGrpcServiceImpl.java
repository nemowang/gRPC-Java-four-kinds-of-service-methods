package com.nemo.grpcexampleserver.service.grpc.impl;

import com.nemo.grpcexampleserver.ExampleServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/11
 */
public class ExampleGrpcServiceImpl extends ExampleServiceGrpc.ExampleServiceImplBase {

    @Override
    public void sayHelloWorld(StringRequest request, StreamObserver<StringResponse> responseObserver) {
        String hello = request.getValue() + " says hello world";
        responseObserver.onNext(StringResponse.newBuilder().setValue(hello).build());
        responseObserver.onCompleted();
    }
}
