package com.example.SpringAIDemo.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class TrimDocumentPostProcessor implements DocumentPostProcessor {

    @Override
    public List<Document> process(Query query, List<Document> documents) {
        List<Document> resultDocuments = documents.stream()
                .map(document ->
                        document.mutate()
                                .text(document.getText()
                                        .replace(" ", "")
                                        .replace("\n", ""))
                                .build())
                .collect(Collectors.toList());
        return resultDocuments;
    }
}
