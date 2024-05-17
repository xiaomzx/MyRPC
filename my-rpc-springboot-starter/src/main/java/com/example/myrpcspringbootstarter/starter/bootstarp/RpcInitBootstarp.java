package com.example.myrpcspringbootstarter.starter.bootstarp;

import com.example.myrpcspringbootstarter.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.service.tcp.VertxTcpServer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Spring初始化时执行，初始化Rpc框架
 */
@Slf4j
public class RpcInitBootstarp implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
       //获取Enable注解的值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");
//Rpc框架初始化
        RpcApplication.init();

        //启动服务器
        if(needServer){
            //启动TCP服务
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPost());
        }else{
            log.info("不启动 Server");
        }
    }
}
