package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/16
 */
@RestController
@RequestMapping("ServerSideStream")
public class ServerSideStreamController {

    @GrpcClient("server-service")
    private ServerSideStreamServiceGrpc.ServerSideStreamServiceBlockingStub blockingStub;

    // TODO APIs
}
