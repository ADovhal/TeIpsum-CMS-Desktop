package com.teipsum.shopcmsdesktop.admin.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teipsum.shopcmsdesktop.admin.model.AdminRegisterRequest;
import com.teipsum.shopcmsdesktop.auth.TokenStore;
import com.teipsum.shopcmsdesktop.config.AppConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AdminService {

    private static final String REGISTER_ADMIN_PATH = "/auth/register_admin";

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
            .build();

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void registerAdmin(AdminRegisterRequest request) throws Exception {
        String json = mapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + REGISTER_ADMIN_PATH))
                .timeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + TokenStore.getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 200) {
            return;
        } else if (response.statusCode() == 401) {
            throw new SecurityException("Unauthorized — please login again");
        } else if (response.statusCode() == 403) {
            throw new SecurityException("Forbidden — admin role required");
        } else if (response.statusCode() == 409) {
            String errorMsg = mapper.readTree(response.body())
                    .path("error").asText("Email already exists");
            throw new IllegalArgumentException(errorMsg);
        } else {
            throw new RuntimeException("Server error: " + response.statusCode()
                    + " — " + response.body());
        }
    }
}