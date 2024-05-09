package org.example.rpc.config;

import lombok.Data;

@Data
public class RpcConfig {
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
