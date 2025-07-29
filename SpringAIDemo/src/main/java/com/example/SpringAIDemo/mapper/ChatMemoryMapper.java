package com.example.SpringAIDemo.mapper;

import com.example.SpringAIDemo.entity.ChatMemory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMemoryMapper {

    // 插入一条聊天记录
    int insertChatMemory(ChatMemory chatMemory);

    // 根据 conversationId 查询所有聊天记录，按时间升序
    List<ChatMemory> getChatMemoriesByConversationId(@Param("conversationId") String conversationId);

    // 根据 conversationId 删除聊天记录
    int deleteByConversationId(@Param("conversationId") String conversationId);
}
