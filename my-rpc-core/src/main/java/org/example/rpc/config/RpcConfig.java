package org.example.rpc.config;

import lombok.Data;
import org.example.rpc.fault.retry.RetryStrategyKeys;
import org.example.rpc.loadbalancer.LoadBalancerKeys;
import org.example.rpc.serializer.SerializerKeys;

@Data
public class RpcConfig {

    //重试策略
    private String retryStrategy = RetryStrategyKeys.NO;
    //注册中心配置
    RegistryConfig registryConfig = new RegistryConfig();
    //序列化器
    private String serializer = SerializerKeys.JDK;
    /**
     * 负载均衡器
     *
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    /**
     * 模拟调用
     */
    private boolean mock = false;
    private String name = "my-rpc";
    private String version = "1.0";
    //服务器主机名
    private String serverHost = "localhost";
    //服务器端口
    private Integer serverPost = 8080;
}
