package com.example.zookeeperconfig.env;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/18
 * 加载配置成PropertySource
 */
public interface PropertySourceLocator {
    PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext);

    default Collection<PropertySource<?>> locateCollection(Environment environment, ConfigurableApplicationContext applicationContext){
        return locateCollections(this, environment, applicationContext);
    }

    static Collection<PropertySource<?>> locateCollections(PropertySourceLocator locator, Environment environment, ConfigurableApplicationContext applicationContext) {
        PropertySource<?> propertySource = locator.locate(environment, applicationContext);
        if (propertySource == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(propertySource);
    }
}
