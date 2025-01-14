package org.oussama.backend.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    // Generate embedding for a single query string
    public double[] generateEmbeddingForQuery(String query) {
        try {
            // Example: Generate a 128-dimension embedding
            double[] embedding = new double[128];
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] = Math.random(); // Replace with actual embedding logic
            }
            return embedding;
        } catch (Exception e) {
            System.err.println("Failed to generate embedding for query: " + query);
            return null;
        }
    }

    // Generate embeddings for multiple chunks
    public List<double[]> generateEmbeddings(List<String> textChunks) {
        List<double[]> embeddings = new ArrayList<>();
        for (String chunk : textChunks) {
            double[] embedding = generateEmbeddingForQuery(chunk);
            if (embedding != null) {
                embeddings.add(embedding);
            } else {
                System.err.println("Skipping chunk due to embedding failure: " + chunk);
            }
        }
        return embeddings;
    }
}
