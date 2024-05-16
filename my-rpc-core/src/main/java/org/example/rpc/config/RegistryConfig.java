package org.example.rpc.config;

import lombok.Data;
import org.example.rpc.loadbalancer.LoadBalancerKeys;

@Data
public class RegistryConfig {


    /**
     * 注册中心类别
     */
    private String registry="etcd";
    /**
     * 注册中心地址
     */
    private String addr = "http://localhost:2379";
    /**
     * 用户名
     */
    private String user;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 超时时间(毫秒）
     */
    private Long timeout = 10000L;

}
