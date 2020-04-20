package com.nemo.grpcexampleclient.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/20
 */
public interface ClientSideStreamService {
    /**
     * 客户端流式传输 - 字符串
     * @return
     */
    String clientStreamString();

    /**
     * 客户端流式传输 - bytes
     * @param file
     * @return
     */
    String clientStreamBytes(MultipartFile file);
}
