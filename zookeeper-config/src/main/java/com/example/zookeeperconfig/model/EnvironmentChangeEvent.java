package com.example.zookeeperconfig.model;

import org.springframework.context.ApplicationEvent;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/22
 */
public class EnvironmentChangeEvent extends ApplicationEvent {

    public EnvironmentChangeEvent(Object source) {
        super(source);
    }
}
