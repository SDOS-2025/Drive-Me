package com.example.driveme.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String getResponse(List<Map<String, String>> conversationHistory) {
        // Build "contents" list as Gemini expects
        List<Map<String, Object>> contents = new ArrayList<>();

        for (Map<String, String> exchange : conversationHistory) {
            String role = exchange.get("role");
            String message = exchange.get("message");

            // Gemini only accepts "user" and "model" roles
            if (!role.equals("user") && !role.equals("model")) continue;

            Map<String, Object> contentItem = Map.of(
                "role", role,
                "parts", List.of(Map.of("text", message))
            );
            contents.add(contentItem);
        }

        // Build final request body
        Map<String, Object> request = Map.of("contents", contents);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // API URL
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        try {
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Extract reply from response
            @SuppressWarnings("null")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                return parts.get(0).get("text");
            }

            return "No response from Gemini.";
        } catch (Exception e) {
            return "Oops! Error talking to Gemini: " + e.getMessage();
        }
    }
}
