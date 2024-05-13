package org.example.rpc.registry;

import org.example.rpc.config.RegistryConfig;
import org.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心
 */
public interface Registry {

    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（服务端）
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 注销服务（服务端）
     * @param serviceMetaInfo
     */
    void unregister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscover(String serviceKey);

    /**
     * 服务销毁
     */
    void destory();

    /**
     * 心跳检测
     */
    void heartBeat();

}
