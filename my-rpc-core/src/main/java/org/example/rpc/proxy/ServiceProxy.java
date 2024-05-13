package org.example.rpc.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.example.rpc.config.RegistryConfig;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.constant.RpcApplication;
import org.example.rpc.constant.RpcConstant;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.serializer.JDKSerializer;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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

            //发请求
            HttpResponse httpResponse = HttpRequest.post(selectServiceMetaInfo.getServiceAddress()).body(bytes).execute();
            byte[] result = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
            return  rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
