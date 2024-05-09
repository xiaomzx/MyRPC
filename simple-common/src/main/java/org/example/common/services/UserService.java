package org.example.common.services;

import org.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {
    /**
     * 获取用户
     * @param user
     * @return
     */
    User getUser(User user);

    default short getNnumber(){
        return 1;
    }
}
