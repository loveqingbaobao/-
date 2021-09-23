package com.example.zookeeperconfig.config;

import com.example.zookeeperconfig.env.PropertySourceLocator;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/18
 * ApplicationContextInitializer接口是spring容器刷新之前执行的一个回调函数
 * 通常用于需要对应用程序上下文进行编程初始化，比如注册相关属性配置等
 * 可以通过在启动main函数中，sringApplication.addInitializers()方法加入
 * 也可通过spring.factories中指定加入
 */
public class ConfigCenterApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final List<PropertySourceLocator> propertySourceLocators;

    public ConfigCenterApplicationContextInitializer() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        // 加载propertySourceLocator的所有扩展实现，SPI
        propertySourceLocators = new ArrayList<>(SpringFactoriesLoader.loadFactories(PropertySourceLocator.class, classLoader));
    }

    /**
     * 动态加载自定义配置或者中心话配置到spring Enviroment中
     * @param configurableApplicationContext
     */
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for (PropertySourceLocator locator : this.propertySourceLocators) {
            Collection<PropertySource<?>> sources = locator.locateCollection(environment, configurableApplicationContext);
            if (sources == null || sources.size() == 0) {
                continue;
            }
            for (PropertySource<?> p : sources) {
                mutablePropertySources.addLast(p);
            }
        }
    }
}
