package com.example.SpringAIDemo.controller;

import com.example.SpringAIDemo.tools.DateTimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/user/chat/ollama")
@Slf4j
public class ChatController {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private final JdbcChatMemoryRepository chatMemoryRepository;

    @Autowired
    public ChatController(OllamaChatModel chatModel, ChatMemory chatMemory, JdbcChatMemoryRepository chatMemoryRepository) {
        this.chatClient = ChatClient.create(chatModel);
        this.chatMemory = chatMemory;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    /**
     * 普通调用，返回一次性回复
     */
    @GetMapping("/base")
    public Map<String, String> baseQuery(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        log.info("chat get msg {}", message);
        ChatClientResponse response = chatClient
                .prompt()
                .user(message)
                .call().chatClientResponse();
        log.info("Chat response: {}", response);
        String result = response.chatResponse().getResult().getOutput().getText();
        log.info("Generated text: {}", result);
        return Map.of("generation", result);
    }

    /**
     * 普通调用，返回一次性回复
     */
    @GetMapping("/basicTools")
    public String baseQueryTools(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        log.info("chat get msg {}", message);
        ChatClientResponse response = chatClient
                .prompt()
                .user(message)
                .tools(new DateTimeTools())
                .call().chatClientResponse();
        log.info("Chat response: {}", response);
        String result = response.chatResponse().getResult().getOutput().getText();
        log.info("Generated text: {}", result);
        return result;
    }

    /**
     * 流式输出纯文本
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamQuery(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message, @RequestParam (value = "conversationId", defaultValue = "007") String conversationId) {
        log.info("chat get msg {}", message);

        return chatClient
                .prompt()
                .user(message)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 通过监听器传入会话ID参数，这里我们传固定值007
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .chatResponse()
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return text != null ? text : "";
                })
                .map(text -> ServerSentEvent.builder(text).build()) // 包装为 SSE 格式
                .doOnNext(text -> log.info("Generated text: {}", text));
    }

    @GetMapping("/streamTools")
    public Flux<String> streamQueryTools(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        log.info("chat get msg {}", message);
        return chatClient
                .prompt()
                .user(message)
                .tools(new DateTimeTools())
                .stream()
                .chatResponse()
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return text != null ? text : "";
                })
                .doOnNext(text -> log.info("Generated text: {}", text));
    }

    @RequestMapping("/memory")
    public String memory(String message) {
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 通过监听器传入会话ID参数，这里我们传固定值007
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "007"))
                .user(message)
                .call()
                .content();
    }

    @RequestMapping("/jdbc")
    public String jdbc(String message) {
        MessageWindowChatMemory jdbcChatMemory = MessageWindowChatMemory.builder().chatMemoryRepository(this.chatMemoryRepository).build();
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(jdbcChatMemory).build())
                // 通过监听器传入会话ID参数，这里我们传固定值007
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "007"))
                .user(message)
                .call()
                .content();
    }
}
