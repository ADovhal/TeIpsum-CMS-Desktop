package com.teipsum.shopcmsdesktop.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teipsum.shopcmsdesktop.auth.TokenStore;
import com.teipsum.shopcmsdesktop.config.AppConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
            .build();

    public static final ObjectMapper mapper = new ObjectMapper();

    public static HttpResponse<String> post(String path, Object body) throws Exception {
        String json = mapper.writeValueAsString(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + path))
                .timeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
                .header("Content-Type", "application/json");

        if (TokenStore.isLoggedIn()) {
            builder.header("Authorization", "Bearer " + TokenStore.getAccessToken());
        }

        HttpRequest request = builder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + path))
                .timeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
                .header("Authorization", "Bearer " + TokenStore.getAccessToken())
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + path))
                .timeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
                .header("Authorization", "Bearer " + TokenStore.getAccessToken())
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
