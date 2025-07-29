package com.example.SpringAIDemo.controller;

import com.example.SpringAIDemo.constant.JwtClaimsConstant;
import com.example.SpringAIDemo.properties.JwtProperties;
import com.example.SpringAIDemo.result.Result;
import com.example.SpringAIDemo.service.UserService;
import com.example.SpringAIDemo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<String> login(@RequestParam String username, @RequestParam String password) {
        Long uId = userService.login(username, password);
        if (uId < 0) {
            log.warn("用户登录失败，用户名或密码错误");
            return Result.error("用户名或密码错误");
        }
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, uId);

        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        return Result.success(token);
    }
}
