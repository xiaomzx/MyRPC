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
import org.example.rpc.fault.retry.RetryStrategy;
import org.example.rpc.fault.retry.RetryStrategyFactory;
import org.example.rpc.loadbalancer.LoadBalancer;
import org.example.rpc.loadbalancer.LoadBalancerFactory;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.model.ServiceMetaInfo;
import org.example.rpc.protocol.*;
import org.example.rpc.registry.Registry;
import org.example.rpc.registry.RegistryFactory;
import org.example.rpc.serializer.JDKSerializer;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;
import org.example.rpc.service.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstant(rpcConfig.getLoadBalancer());
            Map<String,Object> requestParams = new HashMap<>();
            //以方法名计算出哈希值，如果使用一致性哈希环做负载均衡，则该方法只会有同一服务器处理
            requestParams.put("methodName",request.getMethodName());
            ServiceMetaInfo selectServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            //重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
            RpcResponse response = retryStrategy.doRetry(() -> VertxTcpClient.doRequest(request, selectServiceMetaInfo));


            //发送TCP请求
//            RpcResponse response = VertxTcpClient.doRequest(request, selectServiceMetaInfo);
            return response.getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
