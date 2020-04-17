package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleclient.service.ServerSideStreamService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/16
 */
@RestController
@RequestMapping("ServerSideStream")
public class ServerSideStreamController {

    @Autowired
    private ServerSideStreamService serverSideStreamService;

    @PostMapping("serverStreamString")
    @ApiOperation(value = "服务端流式传输 - 字符串")
    public String serverStreamString() {
        return serverSideStreamService.serverStreamString();
    }

    // TODO APIs

}
