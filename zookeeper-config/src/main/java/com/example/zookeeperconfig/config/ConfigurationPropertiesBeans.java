package com.example.zookeeperconfig.config;

import com.example.zookeeperconfig.annotation.RefreshScope;
import com.example.zookeeperconfig.model.FieldPair;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/22
 *
 */
@Component
public class ConfigurationPropertiesBeans implements BeanPostProcessor {
    private Map<String, List<FieldPair>> fieldMapper = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 如果当前类被RefreshScope注解标记， 表明需要动态更新配置
        if (beanClass.isAnnotationPresent(RefreshScope.class)) {
            for (Field field : beanClass.getDeclaredFields()) {
                Value value = field.getAnnotation(Value.class);
                RefreshScope refreshScope = field.getAnnotation(RefreshScope.class);
                if (value == null || refreshScope == null) {
                    // 未加@Value注解的字段忽略
                    continue;
                }
                List<String> keyList = getPropertyKey(value.value(), 0);
                for(String key : keyList) {
                    fieldMapper.computeIfAbsent(key, (k) -> new ArrayList<>())
                            .add(new FieldPair(bean, field, value.value()));
                }
            }
        }
        // bean返回到IOC容器
        return bean;
    }

    /**
     * value="${xxx.xxx:yyyy}"
     * @param value
     * @param begin
     * @return
     */
    private List<String> getPropertyKey(String value, int begin) {
        int start = value.indexOf("${", begin) + 2;
        if (start < 2) {
            // 未找到得到情况下
            return Lists.newArrayList();
        }
        int middle = value.indexOf(":", start);
        int end = value.indexOf("}", start);
        String key;
        if (middle > 0 && middle < end) {
            key = value.substring(start, middle);
        } else {
            key = value.substring(start, end);
        }
        List<String> keys = getPropertyKey(value, end);
        keys.add(key);
        return keys;
    }

    public Map<String, List<FieldPair>> getFieldMapper() {
        return fieldMapper;
    }
}
