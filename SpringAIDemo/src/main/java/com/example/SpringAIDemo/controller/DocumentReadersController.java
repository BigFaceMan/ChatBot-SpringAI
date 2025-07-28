package com.example.SpringAIDemo.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/document/readers")
public class DocumentReadersController {

    VectorStore vectorStore;

    public DocumentReadersController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // 按段落读取
    @RequestMapping("/pdf")
    public String pdf() {
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader("classpath:exam.pdf",
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        List<Document> documentList = pdfReader.read();
        vectorStore.add(documentList);
        return "success";
    }
}
