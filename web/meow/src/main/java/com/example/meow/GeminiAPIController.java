package com.example.meow;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAPIController {
    public static String callGeminiAPI(String text_in_pdf_file) throws IOException, InterruptedException {

        final String APIKEY=AppConfig.getApiKey(); // Replace with your AI Studio key

        final String ENDPOINT = AppConfig.getApiUrl()+APIKEY;

        String requestBody = String.format("""
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "請幫我分析這份檔案的文字：%s"
                }
              ]
            }
          ]
        }
        """,text_in_pdf_file);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        JSONArray candidates = json.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONArray parts = firstCandidate.getJSONObject("content").getJSONArray("parts");
        String resultText = parts.getJSONObject(0).getString("text");
        return resultText;
    }

}
