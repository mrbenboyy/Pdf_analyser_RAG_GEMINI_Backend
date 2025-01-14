package org.oussama.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {
    private static final String API_KEY = "AIzaSyCcIF8mTHwH2G21EHbWEKQg5kCln2M2RnY";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public String generateResponse(String prompt) {
        try {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Create request body according to Gemini API structure
            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode content = objectMapper.createObjectNode();
            ObjectNode part = objectMapper.createObjectNode();

            part.put("text", prompt);
            content.putArray("parts").add(part);
            requestBody.putArray("contents").add(content);

            // Log request for debugging
            System.out.println("Request to Gemini API: " + requestBody.toString());

            // Make the request
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            String responseString = restTemplate.postForObject(API_URL, request, String.class);

            // Log raw response for debugging
            System.out.println("Raw Gemini API Response: " + responseString);

            // Parse the response
            JsonNode responseJson = objectMapper.readTree(responseString);

            // Check for candidates array (new Gemini API structure)
            if (responseJson.has("candidates") && !responseJson.get("candidates").isEmpty()) {
                JsonNode firstCandidate = responseJson.get("candidates").get(0);
                if (firstCandidate.has("content") &&
                        firstCandidate.get("content").has("parts") &&
                        !firstCandidate.get("content").get("parts").isEmpty()) {

                    return firstCandidate.get("content")
                            .get("parts")
                            .get(0)
                            .get("text")
                            .asText();
                }
            }

            // If we get here, try alternative response formats
            if (responseJson.has("text")) {
                return responseJson.get("text").asText();
            }

            // If no recognized format is found, throw an error
            throw new RuntimeException("Unexpected API response format: " + responseString);

        } catch (Exception e) {
            System.err.println("Error in GeminiService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        }
    }
}