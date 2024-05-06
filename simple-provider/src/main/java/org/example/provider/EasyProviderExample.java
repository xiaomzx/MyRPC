package org.example.provider;

import org.example.common.services.UserService;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.service.HttpServer;
import org.example.rpc.service.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        //具体提供服务代码
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}
