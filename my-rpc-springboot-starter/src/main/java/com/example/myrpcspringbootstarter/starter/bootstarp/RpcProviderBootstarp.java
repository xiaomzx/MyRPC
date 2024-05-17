package com.example.myrpcspringbootstarter.starter.bootstarp;

import com.example.myrpcspringbootstarter.starter.annotation.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.concurrent.ExecutionException;

@Slf4j
public class RpcProviderBootstarp implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        RpcServer rpcServer = aClass.getAnnotation(RpcServer.class);
        if(rpcServer != null){
            //注册服务
            Class<?> interfaceClass = rpcServer.interfaceClass();
            //默认值处理
        if(interfaceClass == void.class){
            interfaceClass = aClass.getInterfaces()[0];
        }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcServer.serviceVersion();
            //本地注册
            LocalRegistry.register(serviceName,aClass);

            //全局配置
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(String.valueOf(rpcConfig.getServerPost()));
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }

        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean,beanName);
    }
}
