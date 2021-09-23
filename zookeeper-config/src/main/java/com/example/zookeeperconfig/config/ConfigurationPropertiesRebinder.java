package com.example.zookeeperconfig.config;

import com.example.zookeeperconfig.model.EnvironmentChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/22
 * 接收配置更改的通知事件
 */
@Slf4j
@Component
public class ConfigurationPropertiesRebinder implements ApplicationListener<EnvironmentChangeEvent> {
    private ConfigurationPropertiesBeans beans;
    private Environment environment;

    public ConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans, Environment environment) {
        this.beans = beans;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent environmentChangeEvent) {
        log.info("收到enviroment变更事件");
        rebind();
    }

    private void rebind() {
        this.beans.getFieldMapper().forEach((k, v) -> {
            v.forEach(f -> f.resetValue(environment));
        });
    }
}
