package org.example.rpc.constant;

import lombok.extern.slf4j.Slf4j;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.utils.ConfigUtils;

/**
 * 全局维护RPC的配置信息类，通过双检锁单例
 */
@Slf4j
public class RpcApplication {
    private static  volatile RpcConfig rpcConfig;

    public static void init(){
        try{
            rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFALUR_CONFIG_PREFIX);
        }catch (Exception e){
            rpcConfig = new RpcConfig();
        }
         log.info("rpcConfig is init ,config = {}",rpcConfig.toString());

    }

    public static RpcConfig getRpcConfig(){
        if(rpcConfig == null){
            synchronized (RpcConstant.class){
               if(rpcConfig == null){
                   init();
               }
            }
        }
        return rpcConfig;
    }


}
