package com.example.SpringAIDemo.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vector")
public class VectorController {

    @Autowired
    public VectorStore vectorStore;

    @RequestMapping("/add")
    public String add(@RequestParam("content") List<String> contentList) {
        // 将内容转换成Document, 并添一个元数据，检索的时候可以用作过滤字段
        List<Document> documentList = contentList.stream()
                .map(content -> new Document(content, Map.of("meta1", new Random().nextInt(90))))
                .collect(Collectors.toList());
        vectorStore.add(documentList);
        return "ok";
    }

    @RequestMapping("/deleteById")
    public String deleteById(@RequestParam("id") String id) {
        vectorStore.delete(List.of(id));
        return "ok";
    }

    @RequestMapping("/deleteByExpression")
    public String deleteByExpression(@RequestParam("metaId") Integer metaId) {
        // 构建删除元数据中`meta1=11`的表达式
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression filterExpression = b.eq("meta1", metaId).build();
        vectorStore.delete(filterExpression);
        return "ok";
    }

    @RequestMapping("/deleteByString")
    public String deleteByString(String str) {
        // 构建删除元数据中`meta1=71`的表达式
        vectorStore.delete(str);
        return "ok";
    }

    @RequestMapping("/search")
    public String search(String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
//                .similarityThreshold(0.7)
                .topK(5)
                .build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        documents.forEach(document -> System.out.println(document.getId() + " : " + document.getScore() + " : " + document.getText() + " : " + document.getMetadata()));
        return "ok";
    }
}
