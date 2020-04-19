package com.nemo.grpcexampleclient.service;

import java.io.File;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/17
 */
public interface ServerSideStreamService {

    /**
     * 服务端流式传输 - 字符串
     * @return
     */
    String serverStreamString();

    /**
     * 服务端流式传输 - bytes
     * @return
     */
    File serverStreamBytes();
}
