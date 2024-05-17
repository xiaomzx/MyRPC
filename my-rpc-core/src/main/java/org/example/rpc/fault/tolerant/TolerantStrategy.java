package org.example.rpc.fault.tolerant;

import org.example.rpc.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    /**
     *
     * @param context 上下文，用户传递数据
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
