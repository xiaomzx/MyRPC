package org.example.rpc.loadbalancer;

import org.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer{
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()|| serviceMetaInfoList.size()==0){
            return null;
        }
        int size = serviceMetaInfoList.size();
        if(size ==1){
            return  serviceMetaInfoList.get(0);
        }
        //取模轮询
        int index = atomicInteger.getAndIncrement()%serviceMetaInfoList.size();
        return serviceMetaInfoList.get(index);

    }
}
