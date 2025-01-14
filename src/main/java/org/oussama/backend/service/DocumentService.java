package org.oussama.backend.service;

import org.oussama.backend.models.DocumentModel;
import org.oussama.backend.models.ScoredDocument;

import org.oussama.backend.repositories.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Save a document in the database
    public void saveDocument(DocumentModel document) {
        documentRepository.save(document);
    }

    // Find the most similar documents to a query embedding
    public List<DocumentModel> findMostSimilarDocuments(double[] queryEmbedding, int topK) {
        List<DocumentModel> allDocuments = documentRepository.findAll();
        List<ScoredDocument> scoredDocuments = new ArrayList<>();

        for (DocumentModel document : allDocuments) {
            // Skip documents with invalid embeddings
            if (document.getEmbedding() == null || document.getEmbedding().length != queryEmbedding.length) {
                System.out.println("Skipping document with invalid embedding: " + document.getTitle());
                continue;
            }

            double score = cosineSimilarity(queryEmbedding, document.getEmbedding());
            scoredDocuments.add(new ScoredDocument(document, score));
        }

        // Sort documents by similarity and return the top K
        return scoredDocuments.stream()
                .sorted(Comparator.comparingDouble(ScoredDocument::getScore).reversed())
                .limit(topK)
                .map(ScoredDocument::getDocument)
                .toList();
    }

    // Compute cosine similarity between two vectors
    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
