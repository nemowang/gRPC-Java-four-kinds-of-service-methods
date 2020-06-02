package com.nemo.grpcexampleserver.service.grpc.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import com.nemo.grpcexampleserver.BytesRequest;
import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.*;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/20
 */
@Slf4j
@GrpcService
public class ClientSideStreamGrpcService extends ClientSideStreamServiceGrpc.ClientSideStreamServiceImplBase {

    private final String uploadFilePath;

    @Inject
    public ClientSideStreamGrpcService(@Value("${uploadFilePath}") String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    /**
     * 客户端流式传输 - 字符串
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<StringRequest> clientStreamString(StreamObserver<StringResponse> responseObserver) {
        // 使用StringBuilder接收多次从客户端传来的数据
        StringBuilder stringBuilder = new StringBuilder();

        return new StreamObserver<StringRequest>() {
            @Override
            public void onNext(StringRequest request) {
                log.info("clientStreamString onNext request={}", request.getValue());
                // 拼接客户端分批次传输的数据
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
    @SneakyThrows
    @Override
    public StreamObserver<BytesRequest> clientStreamBytes(StreamObserver<StringResponse> responseObserver) {
        File uploadFile = new File(uploadFilePath + System.currentTimeMillis());
        // 使用BufferedOutputStream流接收多次从客户端传来的数据
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(uploadFile));
        StringBuilder fileName = new StringBuilder();


        return new StreamObserver<BytesRequest>() {
            @SneakyThrows
            @Override
            public void onNext(BytesRequest bytesRequest) {
                log.info("clientStreamBytes onNext.");
                // 拼接客户端分批次传输的数据
                bufferedOutputStream.write(bytesRequest.getData().toByteArray());
                if (StringUtils.isEmpty(fileName.toString())) {
                    fileName.append(bytesRequest.getFileName());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("clientStreamBytes onError: ", throwable);
            }

            @SneakyThrows
            @Override
            public void onCompleted() {
                log.info("clientStreamBytes onCompleted.");
                // 把流中保存的数据flush到文件中
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                // 重命名文件，保留源文件的扩展名
                File file = FileUtil.rename(uploadFile, System.currentTimeMillis() + fileName.toString(), true, true);

                StringResponse.Builder resultBuilder = StringResponse.newBuilder();
                resultBuilder.setValue(file.getName());
                responseObserver.onNext(resultBuilder.build());
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * 客户端流式传输 - bytes 服务端通过byte数组接收
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<BytesRequest> clientStreamBytesByte(StreamObserver<StringResponse> responseObserver) {
        StringBuilder fileName = new StringBuilder();
        byte[][] buff = {null};

        return new StreamObserver<BytesRequest>() {
            @Override
            public void onNext(BytesRequest bytesRequest) {
                log.info("clientStreamBytesByte onNext.");
                // 拼接从客户端多次传来的数据
                buff[0] = ArrayUtil.addAll(buff[0], bytesRequest.getData().toByteArray());
                if (StringUtils.isEmpty(fileName.toString())) {
                    fileName.append(bytesRequest.getFileName());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("clientStreamBytesByte onError: {}", throwable);
            }

            @SneakyThrows
            @Override
            public void onCompleted() {
                log.info("clientStreamBytesByte onCompleted.");
                byte[] data = buff[0];
                File uploadFile = new File(uploadFilePath + System.currentTimeMillis() + fileName.toString());
                // 把byte数组通过流转为文件存储到目标路径
                FileOutputStream fos = new FileOutputStream(uploadFile);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                IoUtil.copy(byteArrayInputStream, fos);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                bufferedOutputStream.close();
                fos.close();

                responseObserver.onNext(StringResponse.newBuilder().setValue(uploadFile.getName()).build());
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * 客户端流式传输 - 服务端抛异常
     * @param responseObserver
     * @return
     */
    @Override
    public StreamObserver<StringRequest> clientStreamThrowException(StreamObserver<StringResponse> responseObserver) {
        // 使用StringBuilder接收多次从客户端传来的数据
        StringBuilder stringBuilder = new StringBuilder();

        return new StreamObserver<StringRequest>() {
            @Override
            public void onNext(StringRequest request) {
                log.info("clientStreamThrowException onNext request={}", request.getValue());
                // 拼接客户端分批次传输的数据
                stringBuilder.append(request.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("clientStreamThrowException onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                log.error("clientStreamThrowException onCompleted.");
                // 抛异常
                responseObserver.onError(Status.UNAVAILABLE.withDescription("成功抛出异常").asRuntimeException());
                responseObserver.onCompleted();
            }
        };
    }
}
