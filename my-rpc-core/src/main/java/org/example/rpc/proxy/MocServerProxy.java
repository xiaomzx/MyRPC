package org.example.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock服务代理
 */
@Slf4j
public class MocServerProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据返回值返回对象
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}",method.getName());
        //基本类型
        if(returnType.isPrimitive()){
            if(returnType == boolean.class){
                return true;
            }
            if(returnType == int.class){
                return 0;
            }
            if(returnType == short.class){
                return  (short)0;
            }
            if(returnType == long.class){
                return 0L;
            }
        }
        //对象类型返回空
        return null;
    }
}
