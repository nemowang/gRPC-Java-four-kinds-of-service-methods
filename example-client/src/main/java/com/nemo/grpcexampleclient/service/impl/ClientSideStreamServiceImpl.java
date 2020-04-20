package com.nemo.grpcexampleclient.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.protobuf.ByteString;
import com.nemo.grpcexampleclient.service.ClientSideStreamService;
import com.nemo.grpcexampleserver.BytesRequest;
import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/20
 */
@Slf4j
@Service
public class ClientSideStreamServiceImpl implements ClientSideStreamService {

    // 客户端流式传输使用非阻塞服务
    @GrpcClient("server-service")
    private ClientSideStreamServiceGrpc.ClientSideStreamServiceStub serviceStub;

    @Value("${server.port}")
    protected String serverPort;

    @Value("${server.domain}")
    protected String serverDomain;

    /**
     * 客户端流式传输 - 字符串
     * @return
     */
    @SneakyThrows
    @Override
    public String clientStreamString() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StringBuilder result = new StringBuilder();

        StreamObserver<StringRequest> observer = serviceStub.clientStreamString(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("ClientSideStreamServiceImpl clientStreamString onNext stringResponse:{}", stringResponse);
                result.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("ClientSideStreamServiceImpl clientStreamString onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                log.info("ClientSideStreamServiceImpl clientStreamString onNext onCompleted");
            }
        });

        // 分批次向服务端发送数据
        for (int i = 0; i < 10; i++) {
            StringRequest.Builder builder = StringRequest.newBuilder();
            builder.setValue("客户端流式传输 - 字符串 第" + i + "次发送数据" + System.lineSeparator());
            observer.onNext(builder.build());
        }
        observer.onCompleted();
        countDownLatch.await();

        return result.toString();
    }

    /**
     * 客户端流式传输 - bytes
     * @param file
     * @return
     */
    @SneakyThrows
    @Override
    public String clientStreamBytes(MultipartFile file) {
        if (ObjectUtil.isEmpty(file)) {
            return "请上传文件";
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BytesRequest.Builder builder = BytesRequest.newBuilder();
        builder.setFileName(file.getOriginalFilename())
                .setServerDomain(serverDomain)
                .setServerPort(serverPort);
        log.info("OriginalFileName: " + file.getOriginalFilename());

        StringBuilder result = new StringBuilder();
        StreamObserver<BytesRequest> observer = serviceStub.clientStreamBytes(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("ClientSideStreamServiceImpl clientStreamBytes onNext stringResponse:{}", stringResponse);
                result.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("ClientSideStreamServiceImpl clientStreamBytes onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                log.info("ClientSideStreamServiceImpl clientStreamString onNext onCompleted");
            }
        });

        byte[] data = file.getBytes();
        // 把上传的文件分段，每段512K
        int buffLength = 512 * 1024;
        byte[][] splitData = ArrayUtil.split(data, buffLength);
        // 分批次向服务器发送数据，每次发送512K
        for (byte[] splitDatum : splitData) {
            observer.onNext(builder.setData(ByteString.copyFrom(splitDatum)).build());
        }
        observer.onCompleted();

        countDownLatch.await(1, TimeUnit.MINUTES);
        return result.toString();
    }
}
