package com.example.meow;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${END_POINT_OF_GEMINI_API}")
    private String apiUrlTemp;

    @Value("${GEMINI_API}")
    private String apiKeyTemp;

    private static String apiUrl;
    private static String apiKey;

    @PostConstruct
    public void init() {
        apiUrl = apiUrlTemp;
        apiKey = apiKeyTemp;
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static String getApiKey() {
        return apiKey;
    }
}
