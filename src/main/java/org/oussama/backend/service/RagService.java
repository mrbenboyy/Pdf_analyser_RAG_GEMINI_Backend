package org.oussama.backend.service;

import org.oussama.backend.models.DocumentModel;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    private final DocumentService documentService;
    private final EmbeddingService embeddingService;
    private final GeminiService geminiService;

    public RagService(DocumentService documentService, EmbeddingService embeddingService, GeminiService geminiService) {
        this.documentService = documentService;
        this.embeddingService = embeddingService;
        this.geminiService = geminiService;
    }

    public String generateResponse(String query) {
        try {
            // Log the incoming query
            System.out.println("Processing query: " + query);

            // Generate query embedding
            System.out.println("Generating embedding for query...");
            double[] queryEmbedding = embeddingService.generateEmbeddingForQuery(query);

            // Find similar documents
            System.out.println("Finding similar documents...");
            List<DocumentModel> similarDocuments = documentService.findMostSimilarDocuments(queryEmbedding, 5);

            // Log number of documents found
            System.out.println("Found " + similarDocuments.size() + " similar documents");

            // Combine document contents with safety check
            String context = "";
            if (!similarDocuments.isEmpty()) {
                context = similarDocuments.stream()
                        .map(DocumentModel::getContent)
                        .filter(content -> content != null && !content.trim().isEmpty())
                        .collect(Collectors.joining("\n\n"));
            }

            // Create enhanced prompt
            String enhancedPrompt = String.format("""
                Using the following context, please answer the query. If the context doesn't contain relevant information, 
                respond based on your general knowledge.

                Context:
                %s

                Query:
                %s

                Answer:""", context, query);

            System.out.println("Sending enhanced prompt to Gemini...");

            // Generate response
            String response = geminiService.generateResponse(enhancedPrompt);

            System.out.println("Successfully generated response");
            return response;

        } catch (Exception e) {
            System.err.println("Error in RagService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generating response: " + e.getMessage(), e);
        }
    }
}