package com.nemo.grpcexampleclient.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.nemo.grpcexampleclient.service.ServerSideStreamService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/16
 */
@Slf4j
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

    @GetMapping("serverStreamBytes")
    @ApiOperation(value = "服务端流式传输 - bytes")
    public void serverStreamBytes(HttpServletResponse response) {
        File file = serverSideStreamService.serverStreamBytes();
        String fileType = FileTypeUtil.getType(file);

        // step1. 配置response
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        response.setContentType("application/octet-stream");
        try {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="
                    + URLEncoder.encode("服务端流式传输下载文件." + fileType, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // step2. 实现文件下载
        byte[] buffer = new byte[1024];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             ServletOutputStream os = response.getOutputStream()) {
            int length;
            while ((length = bis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            log.info("服务端流式传输接口 文件下载成功");
        } catch (Exception e) {
            log.info("服务端流式传输接口 文件下载失败");
            e.printStackTrace();
        } finally {
            boolean del = FileUtil.del(file);
            log.info("删除缓冲文件：" + del);
        }
    }
}
