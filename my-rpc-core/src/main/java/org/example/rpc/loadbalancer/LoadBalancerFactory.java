package org.example.rpc.loadbalancer;

import org.example.rpc.spi.SpiLoader;

import java.io.IOException;

public class LoadBalancerFactory {
   static {
        try {
            SpiLoader.load(LoadBalancer.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOADBALANCER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstant(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }
}
