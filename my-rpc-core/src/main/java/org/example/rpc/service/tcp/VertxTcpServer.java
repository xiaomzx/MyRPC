package org.example.rpc.service.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import org.example.rpc.service.HttpServer;

public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建实例
        Vertx vertx = Vertx.vertx();

        //创建http服务器
        NetServer netServer = vertx.createNetServer();
        //处理请求
        netServer.connectHandler(new TcpServerHandler());

        //启动TCP并监听端口
        netServer.listen(port,netServerAsyncResult -> {
            if(netServerAsyncResult.succeeded()){
                System.out.println("Tcp Server Start on port "+ port);
            }
            else{
                System.out.println("Fail to Start Tcp Server on port "+ port);
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
