package org.example;

import org.example.common.model.User;
import org.example.common.services.UserService;
import org.example.rpc.config.RpcConfig;
import org.example.rpc.proxy.ServiceProxyFactory;
import org.example.rpc.serializer.Serializer;
import org.example.rpc.serializer.SerializerFactory;
import org.example.rpc.spi.SpiLoader;
import org.example.rpc.utils.ConfigUtils;

import java.io.IOException;


/**
 * Hello world!
 *
 */
public class ConsumerExample
{
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println( rpc );
        //加载序列化对象
        SpiLoader.load(Serializer.class);
        //动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("小马");
        User newuser = userService.getUser(user);
         newuser = userService.getUser(user);
         newuser = userService.getUser(user);
        if(newuser != null){
            System.out.println("comsuner 用户名："+newuser.getName());

        }
        else{
            System.out.println("newuser == null");
        }

    }
}
