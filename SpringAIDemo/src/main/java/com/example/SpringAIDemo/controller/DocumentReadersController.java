package com.example.SpringAIDemo.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    @PostMapping("/upload-pdf")
    public String uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".pdf")) {
            return "Invalid file. Please upload a PDF.";
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        Path pdfDir = Path.of("pdfs");
        Files.createDirectories(pdfDir); // 确保目录存在

        Path savedFile = pdfDir.resolve(originalFilename);
        Files.copy(file.getInputStream(), savedFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            String filePath = "file:" + savedFile.toAbsolutePath();

//            ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(filePath,
//                    PdfDocumentReaderConfig.builder()
//                            .withPageTopMargin(0)
//                            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
//                                    .withNumberOfTopTextLinesToDelete(0)
//                                    .build())
//                            .withPagesPerDocument(1)
//                            .build());
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(filePath,
                    PdfDocumentReaderConfig.builder()
                            .withPageTopMargin(0)
                            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                    .withNumberOfTopTextLinesToDelete(0)
                                    .build())
                            .withPagesPerDocument(1)
                            .build());
            List<Document> documentList = pdfReader.read();
            vectorStore.add(documentList);
            return "Upload and indexing success!";
        } catch (Exception e) {
            return "Failed to read or index the document: " + e.getMessage();
        }
    }
}
