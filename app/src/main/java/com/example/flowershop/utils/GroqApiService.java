package com.example.flowershop.utils;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GroqApiService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "llama-3.1-8b-instant";

    private final ExecutorService executor;
    private final Handler mainHandler;

    public GroqApiService() {
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void sendMessage(String userMessage, String flowerContext, Callback callback) {
        executor.execute(() -> {
            try {
                String response = callGroqApi(userMessage, flowerContext);
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            }
        });
    }

    private String callGroqApi(String userMessage, String flowerContext) throws Exception {
        if (flowerContext == null || flowerContext.isEmpty()) {
            flowerContext = "Chưa có dữ liệu hoa trong shop.";
        }

        URL url = new URL(GROQ_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        String systemPrompt = "Bạn là trợ lý ảo của FlowerShop - một cửa hàng hoa tươi. " +
                "Nhiệm vụ của bạn là tư vấn cho khách hàng về các loại hoa, giá cả, dịp tặng hoa, giao hàng, thanh toán. " +
                "Hãy trả lời thân thiện, ngắn gọn bằng tiếng Việt. " +
                "Dưới đây là danh sách hoa có sẵn trong shop:\n\n" + flowerContext;

        String jsonBody = new JSONObject()
                .put("model", MODEL)
                .put("messages", new JSONArray()
                        .put(new JSONObject().put("role", "system").put("content", systemPrompt))
                        .put(new JSONObject().put("role", "user").put("content", userMessage)))
                .put("temperature", 0.7)
                .put("max_tokens", 500)
                .toString();

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("API Error: " + responseCode + " - " + conn.getResponseMessage());
        }

        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String responseStr = response.toString();
        if (responseStr.isEmpty()) {
            throw new Exception("Empty response from API");
        }

        JSONObject jsonResponse = new JSONObject(responseStr);

        if (jsonResponse.has("error")) {
            JSONObject error = jsonResponse.getJSONObject("error");
            throw new Exception(error.optString("message", "Unknown error"));
        }

        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices.length() == 0) {
            throw new Exception("No response choices");
        }

        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

    public interface Callback {
        void onSuccess(String response);
        void onError(String error);
    }
}