package org.example.rpc.fault.retry;

public interface RetryStrategyKeys {
    /**
     * 不重试
     */
    String NO="no";

    /**
     * 固定时间间隔
     */
    String FIXEDINTERVAL="fixedinterval";
}
