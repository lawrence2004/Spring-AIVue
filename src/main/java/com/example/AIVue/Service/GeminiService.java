package com.example.AIVue.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
//@AllArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getGeminiResponse(String userPrompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

        // Constructing the request body
        Map<String, Object> request = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        parts.add(Collections.singletonMap("text", userPrompt));

        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);

        request.put("contents", Collections.singletonList(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> candidate = ((List<Map<String, Object>>) response.getBody().get("candidates")).get(0);
                Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
                List<Map<String, Object>> partList = (List<Map<String, Object>>) contentMap.get("parts");
                return (String) partList.get(0).get("text");
            } else {
                return "Error: Gemini API returned unexpected response.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to connect to Gemini API.";
        }
    }

}
