package org.oussama.backend.models;

// Represents a document with an associated similarity score
public class ScoredDocument {
    private final DocumentModel document;
    private final double score;

    public ScoredDocument(DocumentModel document, double score) {
        this.document = document;
        this.score = score;
    }

    public DocumentModel getDocument() {
        return document;
    }

    public double getScore() {
        return score;
    }
}
