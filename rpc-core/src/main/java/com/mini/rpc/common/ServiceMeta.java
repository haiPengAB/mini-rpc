package com.mini.rpc.common;

import lombok.Data;

// 服务元数据信息封装成一个对象，该对象包括服务名称、服务版本、服务地址和服务端口号
@Data
public class ServiceMeta {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

}
