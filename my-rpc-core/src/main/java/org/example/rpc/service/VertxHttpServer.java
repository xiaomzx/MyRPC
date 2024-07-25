package org.example.rpc.service;

import io.vertx.core.Vertx;

public class  VertxHttpServer implements  HttpServer{
    public void doStart(int port) {
        //创建实例
        Vertx vertx = Vertx.vertx();

        //创建http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        //处理请求
//        httpServer.requestHandler(request -> {
//            System.out.println( "Recevice Request : " + request.method()+" "+ request.uri());
//
//            //发送http响应
//            request.response().putHeader("content-type","text/plain")
//                    .end("Hello from vert.x HTTP Server");
//        } );
        httpServer.requestHandler(new HttpServerHandle());

        //监听端口，启动服务器
        httpServer.listen(port,asyncResult ->{
           if(asyncResult.succeeded()){
               System.out.println("server is listening on port "+ port);
           }
           else{
               System.err.println("Fail to start server :"+ asyncResult.cause());
           }
        });
    }
}
