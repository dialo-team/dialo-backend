package com.fit.se.auth.infrastructure.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SMSSender {

    @Value("${esms.api-key}")
    private String apiKey;

    @Value("${esms.secret-key}")
    private String secretKey;

    @Value("${esms.brandname}")
    private String brandname;

    @Value("${esms.callback-url:}")
    private String callbackUrl;

    private static final String API_URL =
            "https://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_post_json/";

    public void send(String target, String otp) {
        try {
            String response = sendOtp(target, otp);
            System.out.println("SMS sent to: " + target);
            System.out.println("eSMS response: " + response);
        } catch (Exception e) {
            System.err.println("Failed to send SMS to: " + target);
            throw new RuntimeException("Failed to send SMS via eSMS", e);
        }
    }

    public String sendOtp(String phone, String otp) throws Exception {
        String content = otp + " la ma xac minh dang ky " + brandname + " cua ban";
        String requestId = java.util.UUID.randomUUID().toString();

        String json = buildRequestBody(phone, content, requestId);

        System.out.println("eSMS request body: " + json);

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 200 && responseCode < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream(),
                        StandardCharsets.UTF_8
                )
        );

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

    private String buildRequestBody(String phone, String content, String requestId) {
        String normalizedPhone = normalizePhoneNumber(phone);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"ApiKey\":\"").append(escapeJson(apiKey)).append("\",")
                .append("\"Content\":\"").append(escapeJson(content)).append("\",")
                .append("\"Phone\":\"").append(escapeJson(normalizedPhone)).append("\",")
                .append("\"SecretKey\":\"").append(escapeJson(secretKey)).append("\",")
                .append("\"Brandname\":\"").append(escapeJson(brandname)).append("\",")
                .append("\"SmsType\":\"2\",")
                .append("\"IsUnicode\":\"0\",")
                .append("\"campaignid\":\"OTP Signup\",")
                .append("\"RequestId\":\"").append(escapeJson(requestId)).append("\"");

        if (callbackUrl != null && !callbackUrl.isBlank()) {
            json.append(",\"CallbackUrl\":\"").append(escapeJson(callbackUrl)).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    private String normalizePhoneNumber(String phone) {
        if (phone == null) return "";
        return phone.trim();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
