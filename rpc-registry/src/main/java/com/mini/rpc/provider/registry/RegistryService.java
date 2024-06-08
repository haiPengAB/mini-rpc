package com.mini.rpc.provider.registry;

import com.mini.rpc.common.ServiceMeta;

import java.io.IOException;

// 通用的注册中心接口

/**
 * 服务注册 register、
 * 服务注销 unRegister、
 * 服务发现 discovery、
 * 注册中心销毁 destroy
 */
public interface RegistryService {

    void register(ServiceMeta serviceMeta) throws Exception;

    void unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    void destroy() throws IOException;
}
