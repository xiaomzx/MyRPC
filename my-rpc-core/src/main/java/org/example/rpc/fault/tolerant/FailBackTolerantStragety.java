package org.example.rpc.fault.tolerant;


import lombok.extern.slf4j.Slf4j;
import org.example.rpc.model.RpcResponse;

import java.util.Map;
@Slf4j
    public class FailBackTolerantStragety implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("降级处理异常",e);
        return new RpcResponse();
    }
}
