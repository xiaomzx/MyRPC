package org.example.rpc.bootstaro;

import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.model.ServiceRegisterInfo;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.service.tcp.VertxTcpServer;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProviderBootStrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceMetaInfoList){
        RpcApplication.init();
        //全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //注册服务
      for(ServiceRegisterInfo serviceRegisterInfo:serviceMetaInfoList){
          String serviceName = serviceRegisterInfo.getServiceName();
          //本地注册
          LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());
          //服务中心注册
          RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
          Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
          ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
          serviceMetaInfo.setServiceName(serviceName);
          serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
          serviceMetaInfo.setServicePort(Integer.toString(rpcConfig.getServerPost()));
          try {
              registry.register(serviceMetaInfo);
          } catch (Exception e) {
              throw new RuntimeException(serviceName+"服务注册失败",e);
          }
      }
        //启动TCP服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPost());
    }
}
