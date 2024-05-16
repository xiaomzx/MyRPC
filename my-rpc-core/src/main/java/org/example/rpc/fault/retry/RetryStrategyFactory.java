package org.example.rpc.fault.retry;

import org.example.rpc.spi.SpiLoader;

import java.io.IOException;

public class RetryStrategyFactory {
    static {
        try {
            SpiLoader.load(RetryStrategy.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    public static  RetryStrategy getInstance(String key){
        return  SpiLoader.getInstance(RetryStrategy.class,key);
    }
}
