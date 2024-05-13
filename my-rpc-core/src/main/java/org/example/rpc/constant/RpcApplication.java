package org.example.rpc.constant;

import lombok.extern.slf4j.Slf4j;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.utils.ConfigUtils;

/**
 * 全局维护RPC的配置信息类，通过双检锁单例
 */
@Slf4j
public class RpcApplication {
    private static  volatile RpcConfig rpcConfig;

    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFALUR_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
         log.info("rpcConfig is init ,config = {}",rpcConfig.toString());

    }

    public static void init(RpcConfig newConfig){
        rpcConfig = newConfig;
        log.info("rpc init ,config={}",newConfig);
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init ,config={}",registryConfig);

        //创建并注册shutdown Hook，JVM退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destory));
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
