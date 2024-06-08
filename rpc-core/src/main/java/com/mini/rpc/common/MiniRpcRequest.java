package com.mini.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * MiniRpcRequest 主要包含 RPC 远程调用需要的必要参数
 */
@Data
public class MiniRpcRequest implements Serializable {
    private String serviceVersion; // 服务版本

    private String className; // 服务接口名

    private String methodName; // 服务方法名

    private Object[] params; // 方法参数列表

    private Class<?>[] parameterTypes; // 方法参数类型列表
}
