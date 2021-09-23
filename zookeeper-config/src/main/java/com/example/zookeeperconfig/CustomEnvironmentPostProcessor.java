package com.example.zookeeperconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/16
 * Allows for customization of the application's {@link Environment} prior to the application context being refreshed.
 * 允许定制应用的上下文的应用环境优于应用的上下文之前被刷新。（意思就是在spring上下文构建之前可以设置一些系统配置）
 * EnvironmentPostProcessor为spring提供的一个初始化配置的接口，可以在spring上下文刷新之前进行相关环境变量的配置
 * 使用它必须在spring.factories文件中指定类的全路径
 */
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private final Properties properties = new Properties();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new ClassPathResource("custom.properties");
        environment.getPropertySources().addLast(loadProperties(resource));
    }


    private PropertySource<?> loadProperties(Resource resource) {
        if (!resource.exists()) {
            throw new RuntimeException("file not exists");
        }
        try {
            properties.load(resource.getInputStream());
            return new PropertiesPropertySource(resource.getFilename(), properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
