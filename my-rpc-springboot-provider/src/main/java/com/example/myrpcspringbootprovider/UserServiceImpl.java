package com.example.myrpcspringbootprovider;

import com.example.myrpcspringbootstarter.starter.annotation.RpcServer;
import org.example.common.model.User;
import org.example.common.services.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcServer
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}