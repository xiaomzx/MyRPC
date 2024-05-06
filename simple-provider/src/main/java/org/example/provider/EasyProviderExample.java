package org.example.provider;

import org.example.rpc.service.HttpServer;
import org.example.rpc.service.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        //todo
        //具体提供服务代码
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}
