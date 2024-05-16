package org.example.rpc.loadbalancer;

import org.example.rpc.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{
    /**
     * hash环
     */
    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();
    /**
     * 节点数
     */
    private static final int VIRUTAL_NODE_NUM = 100;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
       if(serviceMetaInfoList.isEmpty() ){
           return null;
       }
       //构造hash环
        for(ServiceMetaInfo serviceMetaInfo :serviceMetaInfoList){
            for(int i=0;i<VIRUTAL_NODE_NUM;i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }
       //获取哈希值
        int hash = getHash(requestParams);
       //选择最近大于hash的节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null){
            //hash已经是最大了，或者大于最大值返回头节点
            entry= virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    /**
     * 哈希值算法，可自定义
     * @param object
     * @return
     */
    private int getHash(Object object){
        return  object.hashCode();
    }
}
