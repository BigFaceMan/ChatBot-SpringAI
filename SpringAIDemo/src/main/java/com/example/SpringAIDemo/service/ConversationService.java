package com.example.SpringAIDemo.service;

import com.example.SpringAIDemo.entity.ChatMemory;
import com.example.SpringAIDemo.entity.Conversation;

import java.util.List;

public interface ConversationService {
    /**
     * 获取用户的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Conversation> getConversationsByUserId(Long userId);

    /**
     * 创建新的会话
     *
     * @param conversation 会话对象
     * @return 创建成功的会话对象
     */
    Conversation createConversation(Long userId, String title) throws IllegalArgumentException;
    List<ChatMemory> getConversationContent(String conversationId) throws IllegalArgumentException;
}
