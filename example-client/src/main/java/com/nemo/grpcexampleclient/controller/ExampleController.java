package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleserver.ExampleServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.Channel;
import io.swagger.annotations.ApiOperation;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/11
 */
@RestController
@RequestMapping("Test")
public class ExampleController {

    @GrpcClient("server-service")
    private ExampleServiceGrpc.ExampleServiceBlockingStub exampleServiceBlockingStub;

    /*@Autowired
    private GrpcChannelFactory grpcChannelFactory;

    private ExampleServiceGrpc.ExampleServiceBlockingStub exampleServiceBlockingStub;

    @PostConstruct
    public void init() {
        Channel channel = grpcChannelFactory.createChannel("server-service");
        exampleServiceBlockingStub = ExampleServiceGrpc.newBlockingStub(channel);
    }*/

    @PostMapping("sayHelloWorld")
    @ApiOperation(value = "普通方式传输")
    public String sayHelloWorld() {
        StringRequest request = StringRequest.newBuilder().setValue("Grpc").build();
        StringResponse response = exampleServiceBlockingStub.sayHelloWorld(request);
        return response.getValue();
    }
}
