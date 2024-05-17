package com.example.myrpcspringbootconcumer;

import com.example.myrpcspringbootstarter.starter.annotation.RpcReference;
import org.example.common.model.User;
import org.example.common.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("小马");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}