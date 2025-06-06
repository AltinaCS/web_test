package com.example.meow;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${END_POINT_OF_GEMINI_API}")
    private String apiUrlTemp;

    @Value("${GEMINI_API}")
    private String apiKeyTemp;
    @Value("${SQL_URL}")
    private String sqlurlTemp;
    @Value("${SQL_USER}")
    private String userTemp;
    @Value("${SQL_PASSWORD}")
    private String passwordTemp;

    private static String url ;
    private static String user;
    private static String password;

    private static String apiUrl;
    private static String apiKey;

    @PostConstruct
    public void init() {
        apiUrl = apiUrlTemp;
        apiKey = apiKeyTemp;
        url=sqlurlTemp;
        user=userTemp;
        password=passwordTemp;
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }
}
