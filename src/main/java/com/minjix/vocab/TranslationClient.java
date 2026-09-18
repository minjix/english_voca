package com.minjix.vocab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjix.vocab.model.WordEntry;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TranslationClient {
    private static final String API_URL = "https://api.mymemory.translated.net/get";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // 단어를 MyMemory Translation API로 영어→한국어 직접 번역하여 뜻으로 사용
    public Optional<WordEntry> lookup(String word) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                String url = API_URL + "?q=" + URLEncoder.encode(word, StandardCharsets.UTF_8) + "&langpair=en%7Cko";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());

                JsonNode root = mapper.readTree(response.body());
                String translated = root.path("responseData").path("translatedText").asText();

                // 최상위 매치가 저품질 TM 항목이라 원문을 그대로 돌려주는 경우가 있음
                // -> matches 배열(매치 점수 내림차순)에서 원문과 다른 번역을 찾아 대체
                if (isSameAsWord(word, translated)) {
                    translated = findDifferentTranslation(word, root.path("matches"));
                }

                if (translated != null && !translated.isBlank() && !isSameAsWord(word, translated)) {
                    return Optional.of(new WordEntry(word, translated));
                }
                return Optional.empty();
            } catch (Exception e) {
                if (attempt == 3) {
                    System.err.println("[TranslationClient] " + word + " 번역 실패: " + e.getMessage());
                } else {
                    sleep(500);
                }
            }
        }
        return Optional.empty();
    }

    private boolean isSameAsWord(String word, String text) {
        return text == null || text.isBlank() || text.trim().equalsIgnoreCase(word.trim());
    }

    private String findDifferentTranslation(String word, JsonNode matches) {
        for (JsonNode match : matches) {
            String candidate = match.path("translation").asText();
            if (!isSameAsWord(word, candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
