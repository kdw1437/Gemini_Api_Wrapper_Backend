package com.example.demo.service;

import com.example.demo.config.GeminiProperties;
import com.example.demo.dto.request.GeminiRequest;
import com.example.demo.dto.response.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiProperties geminiProperties;
    private final RestTemplate restTemplate;

    /**
     * Call Python microservice to get Gemini API response
     */
    public String getModelResponse(String userMessage) {
        try {
            String pythonServiceUrl = geminiProperties.getPythonService().getUrl();

            // Create request
            GeminiRequest request = GeminiRequest.builder()
                    .userMessage(userMessage)
                    .build();

            // Call Python service
            GeminiResponse response = restTemplate.postForObject(
                    pythonServiceUrl,
                    request,
                    GeminiResponse.class);

            if (response != null && response.getModelResponse() != null) {
                return response.getModelResponse();
            } else {
                throw new RuntimeException("Python 서비스로부터 응답을 받지 못했습니다");
            }
        } catch (Exception e) {
            System.err.println("Python 서비스 호출 실패: " + e.getMessage());
            throw new RuntimeException("AI 응답 생성에 실패했습니다: " + e.getMessage());
        }
    }
}