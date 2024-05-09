package org.example.rpc.proxy;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public  static <T> T getProxy(Class<T> serviceClass){
        return (T)Proxy.newProxyInstance(serviceClass.getClassLoader(),new Class[]{serviceClass},new ServiceProxy());
    }

    public static <T> T getMockProxy(Class<T> mockClass){
        return (T)Proxy.newProxyInstance(mockClass.getClassLoader(),new Class[]{mockClass},new MocServerProxy());
    }
}
