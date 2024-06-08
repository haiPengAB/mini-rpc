package com.mini.rpc.consumer.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String serviceVersion() default "1.0"; // 服务版本

    String registryType() default "ZOOKEEPER"; // 注册中心类型

    String registryAddress() default "127.0.0.1:2181"; // 注册中心地址

    long timeout() default 5000; // 超时时间

}
