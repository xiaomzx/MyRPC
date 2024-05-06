package org.example.provider;

import org.example.common.model.User;
import org.example.common.services.UserService;

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("用户名： " + user.getName());
        return user;
    }
}
