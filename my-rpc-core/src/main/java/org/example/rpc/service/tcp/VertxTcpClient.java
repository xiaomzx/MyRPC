package org.example.rpc.service.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

public class VertxTcpClient {
    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888,"localhost",result->{
            if(result.succeeded()){
                System.out.println("Connect to Tcp Server");
                NetSocket netSocket = result.result();
                //发送数据
                netSocket.write("Hello Server");
                //接收响应
                netSocket.handler(buffer -> {
                    System.out.println("Receive response from server: "+buffer.toString());

                });
            }else{
                System.out.println("Fail to connect Tcp server ");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
