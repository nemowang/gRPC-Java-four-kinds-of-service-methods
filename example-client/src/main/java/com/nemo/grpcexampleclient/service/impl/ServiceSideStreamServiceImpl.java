package com.nemo.grpcexampleclient.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.nemo.grpcexampleclient.service.ServerSideStreamService;
import com.nemo.grpcexampleserver.Empty;
import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

        // 封装接口结果
        Map<String, Object> result = new HashMap<String, Object>() {{
            put("code", 0);
            put("data", resultBuilder.toString());
        }};
        JSON json = JSONUtil.parse(result);
        log.info(json.toStringPretty());
        return json.toStringPretty();
    }
}
