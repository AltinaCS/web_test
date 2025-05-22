package com.example.meow;

import org.springframework.beans.factory.annotation.Value;

public class AppConfig {
    @Value("${END_POINT_OF_GEMINI_API}")
    private static String apiUrl;

    @Value("${GEMINI_API}")
    private static String apiKey;

    public static String getApiKey() {
        return apiKey;
    }

    public static String getApiUrl() {
        return apiUrl;
    }
}
