package com.example.zookeeperconfig.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;

import java.lang.reflect.Field;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/22
 */
@Slf4j
public class FieldPair {
    private PropertyPlaceholderHelper propertyPlaceholderHelper =
            new PropertyPlaceholderHelper("${", "}", ":", true);

    private Object bean;
    private Field field;
    /**
     * 注解的value值
     */
    private String value;

    public FieldPair(Object bean, Field field, String value) {
        this.bean = bean;
        this.field = field;
        this.value = value;
    }

    public void resetValue(Environment environment) {
        boolean access = field.isAccessible();
        if (!access) {
            field.setAccessible(true);
        }
        String resetValue = propertyPlaceholderHelper.replacePlaceholders(value, environment::getProperty);
        try {
            field.set(bean, resetValue);
        } catch (IllegalAccessException e) {
            log.error("属性设置失败： ", e);
        }
    }
}
