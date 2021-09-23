package com.example.zookeeperconfig.annotation;

import java.lang.annotation.*;

/**
 * @Author: zhenghang.xiong
 * @Date: 2021/9/23
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshScope {
}
