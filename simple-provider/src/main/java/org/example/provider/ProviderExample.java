package org.example.provider;

import org.example.common.services.UserService;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.service.HttpServer;
import org.example.rpc.service.VertxHttpServer;
import org.example.rpc.utils.ConfigUtils;

public class ProviderExample {
    public static void main(String[] args) {
        //RPC初始化配置
        RpcApplication.init();
        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        //具体提供服务代码
        HttpServer httpServer = new VertxHttpServer();
        System.out.println(RpcApplication.getRpcConfig());
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPost());

    }
}
