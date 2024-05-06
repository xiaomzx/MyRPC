package org.example.comsumer;

import org.example.common.model.User;
import org.example.common.services.UserService;

public class EasyComsumerExample {
    public static void main(String[] args) {
        //todo 获取具体userservice
        UserService userService = null;
        User user = new User();
        user.setName("小马");
        User newuser = userService.getUser(user);
        if(newuser != null){
            System.out.println("comsuner 用户名："+newuser.getName());

        }
        else{
            System.out.println("newuser == nu;;");
        }


    }
}
