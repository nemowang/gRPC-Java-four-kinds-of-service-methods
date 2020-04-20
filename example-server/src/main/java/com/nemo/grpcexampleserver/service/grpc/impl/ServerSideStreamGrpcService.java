package com.nemo.grpcexampleserver.service.grpc.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.nemo.grpcexampleserver.BytesResponse;
import com.nemo.grpcexampleserver.Empty;
import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/17
 */
@Slf4j
@GrpcService
public class ServerSideStreamGrpcService extends ServerSideStreamServiceGrpc.ServerSideStreamServiceImplBase {

    private final String filePath;

    @Inject
    public ServerSideStreamGrpcService(@Value("${filePath}") String filePath) {
        this.filePath = filePath;
    }

    /**
     * 服务端流式传输 - 字符串
     * @param request
     * @param responseObserver
     */
    @Override
    public void serverStreamString(Empty request, StreamObserver<StringResponse> responseObserver) {
        StringResponse.Builder builder = StringResponse.newBuilder();
        // 通过流分7次向客户端发送数据
        for (int i = 0; i < 7; i++) {
            builder.setValue("服务端流式传输 - 字符串 第" + i + "次发送数据" + System.lineSeparator());
            responseObserver.onNext(builder.build());
        }
        responseObserver.onCompleted();
    }

    /**
     * 服务端流式传输 - bytes
     * @param request
     * @param responseObserver
     */
    @Override
    public void serverStreamBytes(Empty request, StreamObserver<BytesResponse> responseObserver) {
        // 读取resources目录下的文件路径
        ClassPathResource classPathResource = new ClassPathResource(filePath + "knight.png");

        try (FileInputStream fis = new FileInputStream(classPathResource.getFile());
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            // 512K 缓冲区大小，即每次向客户端发送的数据大小
            byte[] buffer = new byte[512 * 1024];
            while (bis.read(buffer) > 0) {
                responseObserver.onNext(BytesResponse.newBuilder().setData(ByteString.copyFrom(buffer)).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("文件大小：" + FileUtil.readableFileSize(classPathResource.getFile()));
        responseObserver.onCompleted();
    }
}
