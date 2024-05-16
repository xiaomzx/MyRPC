package org.example.rpc.loadbalancer;

import org.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {
    /**
     * 选择服务调用
     *
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    public ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
