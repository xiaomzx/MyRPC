package org.example.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.serializer.JDKSerializer;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JDK动态代理
 */
public class ServiceProxy implements InvocationHandler {

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        Serializer serializer = SerializerFactory.getInstant(RpcApplication.getRpcConfig().getSerializer());
        //发请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName("getUser")
                .parameterTypes(method.getParameterTypes())
                .args(args).build();

        try {
            byte[] bytes = serializer.serializer(request);
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bytes).execute();
            byte[] result = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
            return  rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
