package com.mini.rpc.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


// 参数配置 配置文件中
@Data
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {

    private int servicePort;

    private String registryAddr;

    private String registryType;

}
