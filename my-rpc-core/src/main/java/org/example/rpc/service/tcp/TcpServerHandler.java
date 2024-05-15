package org.example.rpc.service.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.protocol.*;
import org.example.rpc.registry.LocalRegistry;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        //处理链接
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer ->{
            //接受请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RpcRequest request =  protocolMessage.getBody();

            //处理请求，构造响应对象
            RpcResponse response = new RpcResponse();

            try {
                //获取要调用的接口实现类
                Class<?> aClass = LocalRegistry.get(request.getServiceName());
                Method method = aClass.getMethod(request.getMethodName(), request.getParameterTypes());
                Object invoke = method.invoke(aClass.newInstance(), request.getArgs());
                //封装返回结果
                response.setData(invoke);
                response.setMessage("ok");
                response.setDataType(method.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
                response.setException(e);
                response.setMessage(e.getMessage());
            }
            //发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            ProtocolMessage responseProtocolMessage = new ProtocolMessage(header,response);
            try {
                Buffer encode = ProtocolEncode.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (Exception e) {
                throw new RuntimeException("协议消息编码错误");
            }

        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
