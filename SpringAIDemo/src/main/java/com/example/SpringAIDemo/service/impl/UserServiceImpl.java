package com.example.SpringAIDemo.service.impl;

import com.example.SpringAIDemo.entity.User;
import com.example.SpringAIDemo.mapper.UserMapper;
import com.example.SpringAIDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public Long login(String username, String password) {
        User user = userMapper.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user.getId();
        }
        return -1l;
    }

    @Override
    public Long register(String username, String password) {
        User existingUser = userMapper.getUserByUsername(username);
        if (existingUser != null) {
            // 用户名已存在
            return -1L;
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        int result = userMapper.insertUser(newUser);
        return result > 0 ? newUser.getId() : -1L;
    }
}
