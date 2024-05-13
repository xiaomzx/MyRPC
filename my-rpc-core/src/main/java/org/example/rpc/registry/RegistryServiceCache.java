package org.example.rpc.registry;

import lombok.Data;
import org.example.rpc.model.ServiceMetaInfo;

import java.util.List;
@Data
public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    private List<ServiceMetaInfo> newServiceCache;

    /**
     * 清空缓存
     */
    public void clearCache(){
        newServiceCache = null;
    }

}
