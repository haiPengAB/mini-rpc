package com.mini.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * MiniRpcResponse 中 data 表示成功状态下返回的 RPC 请求结果，message 表示 RPC 请求调用失败的错误信息。
 */
@Data
public class MiniRpcResponse implements Serializable {
    private Object data;
    private String message;
}
