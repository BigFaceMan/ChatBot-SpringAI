package com.example.SpringAIDemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Conversation {
    private String id;
    private Long userId;
    private String title;
    private LocalDateTime createdAt;
}
