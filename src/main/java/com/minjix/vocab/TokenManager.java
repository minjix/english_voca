package com.minjix.vocab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TokenManager {
    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final File secretsFile;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public TokenManager() throws Exception {
        Properties config = loadConfig();
        this.secretsFile = new File(config.getProperty("secrets.file.path"));
    }

    // refresh token으로 access token 갱신 후 반환. 갱신 시 local.properties 자동 업데이트
    public String refreshAccessToken() throws Exception {
        Properties secrets = loadSecrets();
        String restApiKey = secrets.getProperty("kakao_rest_api_key");
        String refreshToken = secrets.getProperty("kakao_refresh_token");

        String body = "grant_type=refresh_token"
                + "&client_id=" + URLEncoder.encode(restApiKey, StandardCharsets.UTF_8)
                + "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("카카오 토큰 갱신 실패: HTTP " + response.statusCode() + " " + response.body());
        }

        JsonNode result = mapper.readTree(response.body());
        String newAccessToken = result.get("access_token").asText();

        // refresh_token이 응답에 포함된 경우 갱신된 값으로 교체
        String newRefreshToken = result.has("refresh_token")
                ? result.get("refresh_token").asText()
                : refreshToken;

        updateSecrets(secrets, newAccessToken, newRefreshToken);
        return newAccessToken;
    }

    private Properties loadSecrets() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(secretsFile)) {
            props.load(fis);
        }
        return props;
    }

    private void updateSecrets(Properties secrets, String accessToken, String refreshToken) throws Exception {
        secrets.setProperty("kakao_access_token", accessToken);
        secrets.setProperty("kakao_refresh_token", refreshToken);
        try (FileOutputStream fos = new FileOutputStream(secretsFile)) {
            secrets.store(fos, null);
        }
    }

    private Properties loadConfig() throws Exception {
        Properties props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            props.load(is);
        }
        return props;
    }
}
