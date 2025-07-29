package com.example.SpringAIDemo.mapper;

import com.example.SpringAIDemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserById(Long id);
    User getUserByUsername(String username);
    int insertUser(User user);
}
