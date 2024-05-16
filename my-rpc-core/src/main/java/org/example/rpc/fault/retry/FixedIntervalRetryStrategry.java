package org.example.rpc.fault.retry;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.example.rpc.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 *固定间隔重试
 */
@Slf4j
public class FixedIntervalRetryStrategry implements  RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(10L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数：{}", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
