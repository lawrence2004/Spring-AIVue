package com.example.AIVue.Service;

import com.example.AIVue.dto.MockInterview.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GemService {

    @Value("${gemini.api.key}") // Use from application.properties
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chatWithModel(List<ChatMessage> messages) {
        List<Map<String, Object>> contents = new ArrayList<>();

        for (ChatMessage msg : messages) {
            Map<String, Object> part = Map.of("text", msg.getContent());
            Map<String, Object> messageBlock = new HashMap<>();
            messageBlock.put("parts", List.of(part));
            messageBlock.put("role", msg.getSender().equals("user") ? "user" : "model");
            contents.add(messageBlock);
        }

        Map<String, Object> requestBody = Map.of("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GEMINI_URL + apiKey,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> candidates =
                        (List<Map<String, Object>>) response.getBody().get("candidates");

                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                    return parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

        return "⚠ No response from Gemini.";
    }
}