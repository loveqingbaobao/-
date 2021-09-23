package com.example.zookeeperconfig.env;

import com.example.zookeeperconfig.config.NodeDataChangeListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.apache.curator.framework.CuratorFramework;

import java.util.Iterator;
import java.util.Map;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/18
 */
@Slf4j
public class ZookeeperPropertySourceLocator implements PropertySourceLocator{
    private final CuratorFramework curatorFramework;
    private final String CONFIG_NODE= "/custom-config";

    public ZookeeperPropertySourceLocator() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.182.132:2181")
                .connectionTimeoutMs(60000)
                .sessionTimeoutMs(60000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("config")
                .build();
        curatorFramework.start();
    }

    @Override
    public PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext) {
        log.info("开始加载zookeeper中配置信息");
        CompositePropertySource compositePropertySource = new CompositePropertySource("customService");
        try {
            Map<String, Object> dataMap = getRemoteEnvironment();
            MapPropertySource mapPropertySource = new MapPropertySource("customService", dataMap);
            compositePropertySource.addPropertySource(mapPropertySource);
            // 增加节点修改监听器
            addListener(environment, applicationContext);
        } catch (Exception e) {
            log.error("加载配置失败", e);
        }

        return compositePropertySource;

    }

    private void addListener(Environment environment, ConfigurableApplicationContext applicationContext) {
        NodeDataChangeListener nodeDataChangeListener = new NodeDataChangeListener(environment, applicationContext);
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, CONFIG_NODE, CuratorCache.Options.SINGLE_NODE_CACHE);
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forChanges(nodeDataChangeListener).build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
    }

    /**
     * 获取节点配置信息
     * @return
     * @throws Exception
     */
    private Map<String, Object> getRemoteEnvironment() throws Exception {
        String data = new String(curatorFramework.getData().forPath(CONFIG_NODE));
        ObjectMapper objectMapper = new ObjectMapper();
        // 将多层json配置，转成zookeeper.name.xxx的key的形式，最后保存到配置中
        JsonNode jsonNode = objectMapper.readTree(data);
        // 递归拼接成 . 的形式
        Map<String, Object> configs = Maps.newHashMap();
        setProperties(configs, "", jsonNode);
        return configs;
    }

    /**
     * 递归处理json文件，变成properties  .  的形式
     * @param configMap
     * @param parentPath
     * @param rootNode
     */
    public static void setProperties(Map<String, Object> configMap, String parentPath, JsonNode rootNode) {
        Iterator<String> stringIterator = rootNode.fieldNames();
        // 多个key的循环处理
        while (stringIterator.hasNext()) {
            String key = stringIterator.next();
            String tempKey = StringUtils.isEmpty(parentPath)? key : parentPath + "." + key;
            JsonNode secondNode = rootNode.get(key);
            if (secondNode.isArray()) {
                // 数组情况暂不处理
                continue;
            } else if(secondNode.isValueNode()){
                configMap.put(tempKey, secondNode.asText());
            } else if(secondNode.isObject()) {
                setProperties(configMap, tempKey, secondNode);
            }

        }
    }
}
