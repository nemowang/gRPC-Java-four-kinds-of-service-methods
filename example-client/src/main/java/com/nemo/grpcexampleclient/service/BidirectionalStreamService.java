package com.nemo.grpcexampleclient.service;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/22
 */
public interface BidirectionalStreamService {

    /**
     * 双向流式传输 - 字符串
     * @return
     */
    String bidirectionalStreamString();
}
