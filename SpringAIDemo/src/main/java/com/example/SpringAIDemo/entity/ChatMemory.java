package com.example.SpringAIDemo.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ChatMemory {
    private String conversationId;
    private String content;
    private ChatMemoryType type;
    private Timestamp timestamp;

    public enum ChatMemoryType {
        USER,
        ASSISTANT,
        SYSTEM,
        TOOL
    }
}
