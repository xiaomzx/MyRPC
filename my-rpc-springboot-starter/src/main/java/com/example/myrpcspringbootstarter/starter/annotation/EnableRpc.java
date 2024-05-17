package com.example.myrpcspringbootstarter.starter.annotation;

import com.example.myrpcspringbootstarter.starter.bootstarp.RpcConsumerBootstarp;
import com.example.myrpcspringbootstarter.starter.bootstarp.RpcInitBootstarp;
import com.example.myrpcspringbootstarter.starter.bootstarp.RpcProviderBootstarp;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只有使用@EnableRpc这个注解时，才注入三个框架类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcConsumerBootstarp.class, RpcInitBootstarp.class, RpcProviderBootstarp.class})
public @interface EnableRpc {
    boolean needServer() default  true;
}
