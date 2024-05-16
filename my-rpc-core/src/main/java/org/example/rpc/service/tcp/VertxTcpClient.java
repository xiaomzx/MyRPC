package org.example.rpc.service.tcp;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.protocol.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {
    /**
     * 发送请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(Integer.parseInt(serviceMetaInfo.getServicePort()), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {

                        System.err.println("Failed to connect to TCP server , cause = " +result.cause().getMessage());
                        return;
                    }
                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializeeEnum.getEnumByvalue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    // 生成全局请求 ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer encodeBuffer = ProtocolEncode.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (Exception e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 接收响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);

                });

        RpcResponse rpcResponse = responseFuture.get();
        // 记得关闭连接
        netClient.close();
        return rpcResponse;
    }
    public void start(){
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888,"localhost",result->{
            if(result.succeeded()){
                System.out.println("Connect to Tcp Server");
                NetSocket netSocket = result.result();
                //发送数据
//                netSocket.write("Hello Server");
                //模拟粘包半包问题
                for(int i=0;i<1000;i++){
                    Buffer buffered = Buffer.buffer();
                    String str = "Hello Server!Hello Server!Hello Server!";
                    buffered.appendInt(0);
                    buffered.appendInt(str.getBytes().length);
                    buffered.appendBytes(str.getBytes());
                    netSocket.write(buffered);
                }
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
