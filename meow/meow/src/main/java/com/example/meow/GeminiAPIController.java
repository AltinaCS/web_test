package com.example.meow;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiAPIController {
    public static JSONArray loadJobListFromResources(String fileName) throws IOException {
        InputStream is = GeminiAPIController.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new FileNotFoundException("找不到資源檔案：" + fileName);
        }
        String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return new JSONArray(jsonText);
    }
    public static String callGeminiAPI(String text_in_pdf_file) throws IOException, InterruptedException {

        final String APIKEY=AppConfig.getApiKey();
        final String ENDPOINT = AppConfig.getApiUrl();
        String fullUrl = ENDPOINT+APIKEY;
        JSONArray jobList = loadJobListFromResources("cake_jobs_pages.json");
        String escapedText = JSONObject.quote("這是一份從自傳提取出的關鍵字 請幫我從這些關鍵字找出這個人的特點與適合的工作(如果你認為這不是自傳內容，請直接回傳\"非自傳請重新上傳\"，如果你覺得這個人的自傳有缺陷，請把它提點出來)：" + text_in_pdf_file+"另外請幫我從這份檔案比對適合的職缺："+jobList);

        String payload = String.format("""
    {
      "contents": [
        {
          "parts": [
            {
              "text": %s
            }
          ]
        }
      ]
    }
""", escapedText);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        if (json.has("error")) {
            JSONObject error = json.getJSONObject("error");
            System.err.println("API Error: " + error.getString("message"));
            return null; // 或丟出例外 / 給預設值
        }

        if (!json.has("candidates")) {
            System.err.println("No 'candidates' found in the response.");
            return null;
        }
        JSONArray candidates = json.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONArray parts = firstCandidate.getJSONObject("content").getJSONArray("parts");
        String resultText = parts.getJSONObject(0).getString("text");


        return resultText;
    }

}
