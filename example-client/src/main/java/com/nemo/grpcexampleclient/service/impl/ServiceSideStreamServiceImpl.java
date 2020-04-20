package com.nemo.grpcexampleclient.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.nemo.grpcexampleclient.service.ServerSideStreamService;
import com.nemo.grpcexampleserver.BytesResponse;
import com.nemo.grpcexampleserver.Empty;
import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/17
 */
@Slf4j
@Service
public class ServiceSideStreamServiceImpl implements ServerSideStreamService {

    @GrpcClient("server-service")
    private ServerSideStreamServiceGrpc.ServerSideStreamServiceBlockingStub blockingStub;

    /**
     * 服务端流式传输 - 字符串
     * @return
     */
    @Override
    public String serverStreamString() {
        // 通过服务端流式传输接口获取到的数据是继承了Iterator接口的类型
        Iterator<StringResponse> responseIterator = blockingStub.serverStreamString(Empty.newBuilder().build());
        StringBuilder resultBuilder = new StringBuilder();
        while (responseIterator.hasNext()) {
            resultBuilder.append(responseIterator.next().getValue());
        }
        return resultBuilder.toString();
    }

    /**
     * 服务端流式传输 - bytes
     * @return
     */
    @Override
    public File serverStreamBytes() {
        // step1. 接收服务端返回的数据 通过服务端流式传输接口获取到的数据是继承了Iterator接口的类型
        Iterator<BytesResponse> responseIterator = blockingStub.serverStreamBytes(Empty.newBuilder().build());
        String bufferFilePath = "";
        BufferedOutputStream bufferedOutputStream = null;
        while (responseIterator.hasNext()) {
            BytesResponse next = responseIterator.next();
            // step2. 把分段传输的数据写入到 bufferOutputStream 流中
            try {
                if (StringUtils.isEmpty(bufferFilePath)) {
                    bufferFilePath = "./" + UUID.randomUUID().toString() + "serverStreamBytes";
                    FileOutputStream fos = new FileOutputStream(bufferFilePath);
                    bufferedOutputStream = new BufferedOutputStream(fos);
                }
                bufferedOutputStream.write(next.getData().toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (bufferedOutputStream != null) {
                // step3. 接收到全部数据后，把数据flush到文件(bufferFilePath)中
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(bufferFilePath);
    }
}
