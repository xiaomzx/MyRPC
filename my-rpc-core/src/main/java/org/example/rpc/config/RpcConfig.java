package org.example.rpc.config;

import lombok.Data;
import org.example.rpc.serializer.SerializerKeys;

@Data
public class RpcConfig {
    //注册中心配置
    RegistryConfig registryConfig = new RegistryConfig();
    //序列化器
    private String serializer = SerializerKeys.JDK;
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
