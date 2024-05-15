package org.example.rpc.service.tcp;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
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

//        netServer.connectHandler(netSocket -> {
//           //构造parser
//            RecordParser recordParser = RecordParser.newFixed(8);
//            recordParser.setOutput(new Handler<Buffer>() {
//                //初始化
//                int size =-1;
//                //一次性读取
//                Buffer resultBuffer = Buffer.buffer();
//                @Override
//                public void handle(Buffer buffer) {
//                    if(-1 == size){
//                        //读取消息体长度
//                        size = buffer.getInt(4);
//                        recordParser.fixedSizeMode(size);
//                        //写入头信息到结果
//                        resultBuffer.appendBuffer(buffer);
//                    }else {
//                        //写入信息到结果
//                        resultBuffer.appendBuffer(buffer);
//                        System.out.println(resultBuffer.toString());
//                        //重置一轮
//                        recordParser.fixedSizeMode(8);
//                        size=-1;
//                        resultBuffer = Buffer.buffer();
//                    }
//                }
//            });
//            netSocket.handler(recordParser);

//        });


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
