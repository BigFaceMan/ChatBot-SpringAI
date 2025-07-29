package com.example.SpringAIDemo.service.impl;

import com.example.SpringAIDemo.entity.ChatMemory;
import com.example.SpringAIDemo.entity.Conversation;
import com.example.SpringAIDemo.mapper.ChatMemoryMapper;
import com.example.SpringAIDemo.mapper.ConversationMapper;
import com.example.SpringAIDemo.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;
    @Autowired
    private ChatMemoryMapper chatMemoryMapper;
    @Override
    public List<Conversation> getConversationsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return conversationMapper.getConversationsByUserId(userId);
    }

    @Override
    public Conversation createConversation(Long userId, String title) {
        if (userId == null || title == null || title.isEmpty()) {
            throw new IllegalArgumentException("User ID and title cannot be null or empty");
        }

        Conversation conversation = new Conversation();
        conversation.setId(UUID.randomUUID().toString());
        conversation.setUserId(userId);
        conversation.setTitle(title);
        conversation.setCreatedAt(LocalDateTime.now());

        int result = conversationMapper.insertConversation(conversation);
        if (result > 0) {
            return conversation;
        } else {
            throw new RuntimeException("Failed to create conversation");
        }
    }

    @Override
    public List<ChatMemory> getConversationContent(String conversationId) throws IllegalArgumentException {
        if (conversationId == null || conversationId.isEmpty()) {
            throw new IllegalArgumentException("Conversation ID cannot be null or empty");
        }
        List<ChatMemory> chatMemories = chatMemoryMapper.getChatMemoriesByConversationId(conversationId);
        if (chatMemories == null || chatMemories.isEmpty()) {
            throw new IllegalArgumentException("No chat memories found for the given conversation ID");
        }
        return chatMemories;
    }
}
