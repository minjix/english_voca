package com.minjix.vocab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjix.vocab.model.WordEntry;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class KakaoClient {
    private static final String SEND_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private static final String[] NUMBER_EMOJI = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣"};
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public void sendToMe(String accessToken, List<WordEntry> words) throws Exception {
        String message = buildMessage(words);
        String templateJson = mapper.writeValueAsString(Map.of(
                "object_type", "text",
                "text", message,
                "link", Map.of("web_url", "", "mobile_web_url", "")
        ));
        String body = "template_object=" + URLEncoder.encode(templateJson, StandardCharsets.UTF_8);

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(SEND_URL))
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) return;
                throw new RuntimeException("카카오 전송 실패: HTTP " + response.statusCode() + " " + response.body());
            } catch (Exception e) {
                if (attempt == 3) throw e;
                System.err.println("[KakaoClient] 재시도 " + attempt + ": " + e.getMessage());
                Thread.sleep(1000L * attempt);
            }
        }
    }

    private String buildMessage(List<WordEntry> words) {
        StringBuilder sb = new StringBuilder();
        sb.append("🌟 오늘의 영단어 🌟\n");
        for (int i = 0; i < words.size(); i++) {
            WordEntry w = words.get(i);
            sb.append("\n").append(NUMBER_EMOJI[i]).append(" ").append(w.getWord()).append("\n");
            sb.append("    📌 ").append(w.getMeaning()).append("\n");
        }
        sb.append("\n열심히 외워봐요 💪✨");
        return sb.toString();
    }
}
