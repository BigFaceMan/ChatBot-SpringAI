package com.example.SpringAIDemo.service;

public interface UserService {
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回用户ID
     */
    Long login(String username, String password);

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回用户ID
     */
    Long register(String username, String password);
}
