package com.example.zookeeperconfig.config;

import com.example.zookeeperconfig.env.ZookeeperPropertySourceLocator;
import com.example.zookeeperconfig.model.EnvironmentChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.CuratorCacheListenerBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/18
 */
@Slf4j
public class NodeDataChangeListener implements CuratorCacheListenerBuilder.ChangeListener {
    private Environment environment;
    private ConfigurableApplicationContext applicationContext;

    public NodeDataChangeListener(Environment environment, ConfigurableApplicationContext applicationContext) {
        this.environment = environment;
        this.applicationContext = applicationContext;
    }

    @Override
    public void event(ChildData childData, ChildData childData1){
        log.info("收到节点变更事件============");
        String result = new String(childData1.getData());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object>  dataMap = Maps.newHashMap();
        // 将多层json配置，转成zookeeper.name.xxx的key的形式，最后保存到配置中
        try {
            JsonNode jsonNode = objectMapper.readTree(result);
            ZookeeperPropertySourceLocator.setProperties(dataMap, "", jsonNode);
            MapPropertySource mapPropertySource = new MapPropertySource("customService", dataMap);
            ConfigurableEnvironment cfe = (ConfigurableEnvironment) this.environment;
            cfe.getPropertySources().replace("customService", mapPropertySource);
            // 发送数据变更事件  spring的事件发布机制，解耦程序
            applicationContext.publishEvent(new EnvironmentChangeEvent(this));
            log.info("数据变更完成=============");
        } catch (JsonProcessingException e) {
            log.error("更新配置失败", e);
        }
    }
}
