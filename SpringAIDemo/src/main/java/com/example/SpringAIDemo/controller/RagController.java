package com.example.SpringAIDemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rag")
@Slf4j
public class RagController {
    private final ChatClient.Builder chatClientBuilder;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClientBuilder = chatClientBuilder;
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @RequestMapping("/base")
    public String base(String userInput) {
        // 1. 构建查询对象
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userInput)
                .similarityThreshold(0.2)
                .topK(5)
                .build();
        // 2. 搜索相似文档
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        String documentContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        log.info("vector store 查询结果：{}", documentContext);

        // 3. 构建用户输入
        userInput = "参考信息：" +
                documentContext +
                "根据参考信息，回答用户问题，如果无法根据参考信息回答，则回答：“抱歉，我无法回答您的问题”：" +
                userInput;

        // 4. 发送查询
        return chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }

    @RequestMapping("/qa")
    public String qa(String userInput) {
        return chatClient.prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .user(userInput)
                .call()
                .content();
    }

    @RequestMapping("/ra")
    public String ra(String userInput) {
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(userInput)
                .call()
                .content();
    }

    @RequestMapping("/ra/pre/query")
    public String raQueryTransformer(String userInput) {
        // 创建查询转换器, 指定目标语言为中文
        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("chinese")
                .build();

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(queryTransformer)
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(userInput)
                .call()
                .content();
    }

    @RequestMapping("/ra/pre/expander")
    public String raQueryExpander(String userInput) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();

        // 位了看效果，这里调用扩展，并打印扩展结果
        List<Query> queries = queryExpander.expand(new Query(userInput));
        queries.forEach(query -> System.out.println("查询扩展：" + query.text()));

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryExpander(queryExpander)
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(userInput)
                .call()
                .content();
    }

    @RequestMapping("/ra/post")
    public String raPost(String userInput) {
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                // 添加文档后处理
                .documentPostProcessors(new TrimDocumentPostProcessor())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();

        return chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(userInput)
                .call()
                .content();
    }
}
