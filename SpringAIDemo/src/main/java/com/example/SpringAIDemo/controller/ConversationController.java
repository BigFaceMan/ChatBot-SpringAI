package com.example.SpringAIDemo.controller;

import com.example.SpringAIDemo.context.BaseContext;
import com.example.SpringAIDemo.entity.ChatMemory;
import com.example.SpringAIDemo.entity.Conversation;
import com.example.SpringAIDemo.result.Result;
import com.example.SpringAIDemo.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 新建对话
     * @param conversation 请求体中传递的对话信息（title 和 userId）
     * @return 新建的Conversation对象
     */
    @PostMapping("/create")
    public Result<Conversation> createConversation(@RequestBody String title) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        Conversation conversation = conversationService.createConversation(userId, title);
        return Result.success(conversation);
    }
    /**
     * 获取用户的会话列表
     * @return 会话列表
     */
    @GetMapping("/list")
    public Result<List<Conversation>> getConversations() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        List<Conversation> conversations = conversationService.getConversationsByUserId(userId);
        return Result.success(conversations);
    }

    @GetMapping("/content")
    public Result<List<ChatMemory>> getConversationContent(@RequestParam String conversationId) {
        if (conversationId == null || conversationId.isEmpty()) {
            return Result.error("Conversation ID cannot be null or empty");
        }
        try {
            List<ChatMemory> records= conversationService.getConversationContent(conversationId);
            return Result.success(records);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("An error occurred while fetching conversation content: " + e.getMessage());
        }
    }
}
