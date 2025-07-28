package com.example.SpringAIDemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 匹配所有接口路径
                        .allowedOrigins("*") // 允许所有来源（你也可以填 http://localhost:5500）
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*") // 允许所有请求头
                        .allowCredentials(false) // 若前端需要携带 cookie，改为 true
                        .maxAge(3600); // 预检请求缓存时间（秒）
            }
        };
    }
}
