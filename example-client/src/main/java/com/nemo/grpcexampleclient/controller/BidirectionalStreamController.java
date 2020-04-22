package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleclient.service.BidirectionalStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/22
 */
@RestController
@Api(value = "双向流式传输", tags = "双向流式传输")
@RequestMapping("BidirectionalStream")
public class BidirectionalStreamController {

    @Autowired
    BidirectionalStreamService bidirectionalStreamService;

    @ApiOperation(value = "双向流式传输 - 字符串")
    @PostMapping
    public String bidirectionalStreamString() {
        return bidirectionalStreamService.bidirectionalStreamString();
    }
}
