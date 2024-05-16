package org.example.rpc.loadbalancer;

import org.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    private  final Random random = new Random();
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if(size ==0){
            return null;
        }
        if(size ==1){
            return serviceMetaInfoList.get(0);
        }
        int index = random.nextInt(size);
        return serviceMetaInfoList.get(index);

    }
}
