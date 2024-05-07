package org.example.rpc.service;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.registry.LocalRegistry;
import org.example.rpc.serializer.JDKSerializer;
import org.example.rpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServerHandle implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer serializer = new JDKSerializer();
        //日志
        System.out.println("Receive Request :"+ request.method()+"  "+ request.uri());

        //异步处理HTTP请求
        request.bodyHandler(body->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserializer(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //构造相应对象
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为空，直接返回
            if(rpcRequest == null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            //本地注册器获取类，通过反射调用方法
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");

            }  catch (Exception e) {
               e.printStackTrace();
               rpcResponse.setException(e);
               rpcResponse.setMessage(e.getMessage());
            }
            //响应
            doResponse(request,rpcResponse,serializer);
        });

    }

    public  void doResponse(HttpServerRequest request,RpcResponse response,Serializer serializer){
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type","application/json");

        try {
            //序列化
            byte[] bytes = serializer.serializer(response);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
           e.printStackTrace();
           httpServerResponse.end(Buffer.buffer());
        }

    }
}
