package com.example.zookeeperconfig;

import com.example.zookeeperconfig.annotation.RefreshScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/18
 */
@RefreshScope
@RestController
public class ConfigController {
    @Value("${server.ip}")
    private String ip;
    @Value("${server.port}")
    private Integer port;
    @Value("${server.domain}")
    private String domain;

    @Value("${zookeeper.name}")
    @RefreshScope
    private String zookeeperName;

    @Value("${zookeeper.version}")
    @RefreshScope
    private String zookeeperVersion;

    @GetMapping("/config")
    public String getConfig() {
        return ip + ":" + port + "\r\n domain: " + domain;
    }


    @GetMapping("/zookeeperInfo")
    public String getZookeeperInfo() {
        return zookeeperName + ": " + zookeeperVersion;
    }
}
