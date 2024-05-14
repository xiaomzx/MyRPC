package org.example.provider;

import org.example.common.services.UserService;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.service.HttpServer;
import org.example.rpc.service.VertxHttpServer;
import org.example.rpc.service.tcp.VertxTcpClient;
import org.example.rpc.service.tcp.VertxTcpServer;
import org.example.rpc.spi.SpiLoader;
import org.example.rpc.utils.ConfigUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ProviderExample {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //RPC初始化配置
        RpcApplication.init();
        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        //加载SPI对象
        SpiLoader.loadAll();
        //注册服务
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(Integer.toString(rpcConfig.getServerPost()));
        try {
            registry.register(serviceMetaInfo);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        //具体提供服务代码
//        HttpServer httpServer = new VertxHttpServer();
//        System.out.println(RpcApplication.getRpcConfig());
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPost());

        //启动TCP服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);

    }
}
