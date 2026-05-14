package com.teipsum.shopcmsdesktop.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.teipsum.shopcmsdesktop.auth.TokenStore;
import com.teipsum.shopcmsdesktop.auth.model.LoginRequest;
import com.teipsum.shopcmsdesktop.http.ApiClient;

import java.net.http.HttpResponse;

public class AuthService {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String REFRESH_PATH = "/auth/refresh";

    public static void login(String email, String password) throws Exception {
        HttpResponse<String> response = ApiClient.post(
                LOGIN_PATH,
                new LoginRequest(email, password)
        );

        if (response.statusCode() == 200) {
            JsonNode json = ApiClient.mapper.readTree(response.body());

            String access  = json.get("accessToken").asText();
            String refresh = json.has("refreshToken")
                    ? json.get("refreshToken").asText() : null;
            String role    = json.has("role")
                    ? json.get("role").asText() : "ADMIN";

            if (!role.contains("ADMIN")) {
                throw new SecurityException("Access denied: not an admin account");
            }

            TokenStore.save(access, refresh, role);

        } else if (response.statusCode() == 401) {
            throw new SecurityException("Invalid email or password");
        } else {
            throw new RuntimeException("Server error: " + response.statusCode());
        }
    }

    public static void logout() {
        TokenStore.clear();
    }
}