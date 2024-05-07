package org.example.comsumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.example.common.model.User;
import org.example.common.services.UserService;
import org.example.rpc.model.RpcRequest;
import org.example.rpc.model.RpcResponse;
import org.example.rpc.serializer.JDKSerializer;
import org.example.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理,就是直接写个实现类
 */
public class UserServiceProxy implements UserService {

    public User getUser(User user) {
        Serializer serializer = new JDKSerializer();
        //发请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user}).build();

        try {
            byte[] bytes = serializer.serializer(user);
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bytes).execute();
            byte[] result = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
            return null;
    }
}
