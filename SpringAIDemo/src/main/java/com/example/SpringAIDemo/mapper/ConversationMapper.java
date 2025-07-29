package com.example.SpringAIDemo.mapper;

import com.example.SpringAIDemo.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConversationMapper {
    List<Conversation> getConversationsByUserId(Long userId);
    int insertConversation(Conversation conversation);
}
