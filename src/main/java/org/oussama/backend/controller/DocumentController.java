package org.oussama.backend.controller;

import org.oussama.backend.models.DocumentModel;
import org.oussama.backend.service.DocumentService;
import org.oussama.backend.service.PDFProcessingService;
import org.oussama.backend.service.EmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/documents")
public class DocumentController {

    private final PDFProcessingService pdfProcessingService;
    private final EmbeddingService embeddingService;
    private final DocumentService documentService;

    public DocumentController(PDFProcessingService pdfProcessingService,
                              EmbeddingService embeddingService,
                              DocumentService documentService) {
        this.pdfProcessingService = pdfProcessingService;
        this.embeddingService = embeddingService;
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPDF(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file to a temporary location
            File tempFile = File.createTempFile("uploaded", ".pdf");
            file.transferTo(tempFile);

            // Extract text chunks from the PDF
            List<String> textChunks = pdfProcessingService.extractTextChunksFromPDF(tempFile, 1000);

            // Generate embeddings for the chunks
            List<double[]> embeddings = embeddingService.generateEmbeddings(textChunks);

            // Save each chunk and its embedding in MongoDB
            for (int i = 0; i < textChunks.size(); i++) {
                DocumentModel document = new DocumentModel();
                document.setTitle(file.getOriginalFilename() + "_chunk_" + (i + 1));
                document.setContent(textChunks.get(i));
                document.setEmbedding(embeddings.get(i));
                documentService.saveDocument(document);
            }

            return ResponseEntity.ok("PDF uploaded and processed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing the PDF: " + e.getMessage());
        }
    }
}
