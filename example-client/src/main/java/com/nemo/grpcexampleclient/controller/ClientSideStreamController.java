package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
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
public class ClientSideStreamController {

    @GrpcClient("server-service")
    private ClientSideStreamServiceGrpc.ClientSideStreamServiceBlockingStub blockingStub;

    // TODO APIs
}
