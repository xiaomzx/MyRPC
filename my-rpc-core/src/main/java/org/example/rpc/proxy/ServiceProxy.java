package org.example.rpc.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.constant.RpcConstant;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.protocol.*;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.serializer.JDKSerializer;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * JDK动态代理
 */
public class ServiceProxy implements InvocationHandler {

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        Serializer serializer = SerializerFactory.getInstant(RpcApplication.getRpcConfig().getSerializer());
        //构建请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName("getUser")
                .parameterTypes(method.getParameterTypes())
                .args(args).build();

        try {
            //序列化
            byte[] bytes = serializer.serializer(request);
            //从注册中心获取地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscover(serviceMetaInfo.getServiceKey());
            if(CollectionUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }
            //先取第一个，负载均衡以后再做
            ServiceMetaInfo selectServiceMetaInfo = serviceMetaInfoList.get(0);
            //发送TCP请求
            Vertx vertx = Vertx.vertx();
            NetClient netClient = vertx.createNetClient();
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
            netClient.connect(Integer.parseInt(selectServiceMetaInfo.getServicePort()),selectServiceMetaInfo.getServiceHost(),
                    result->{
                if(result.succeeded()){
                    System.out.println("Connect to TCP Server");
                    NetSocket socket = result.result();
                    //构造对象，发送数据
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializeeEnum.getEnumByvalue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(request);
                    try {
                        Buffer encode = ProtocolEncode.encode(protocolMessage);
                        socket.write(encode);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    //接收响应
                    socket.handler(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> decode = (ProtocolMessage<RpcResponse>)ProtocolMessageDecoder.decode(buffer);
                            responseFuture.complete(decode.getBody());
                        } catch (Exception e) {
                            throw new RuntimeException("协议信息代码错误");
                        }
                    });
                }else{
                    System.out.println("Fail to connect TCP server");
                }
                    });

            RpcResponse response = responseFuture.get();
            //关闭连接
            netClient.close();
            return  response.getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
