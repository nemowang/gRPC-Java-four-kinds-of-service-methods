package com.nemo.grpcexampleserver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/11
 */
@Configuration
public class GrpcConfig {
    @Bean
    ManagedChannel channel(@Value("${server.name}") String name,
                           @Value("${port}") Integer port){
        return ManagedChannelBuilder.forAddress(name, port)
                .usePlaintext()
                .build();
    }

    @Bean
    ExampleServiceGrpc.ExampleServiceBlockingStub memberListServiceBlockingStub(ManagedChannel channel){
        return ExampleServiceGrpc.newBlockingStub(channel);
    }
}
