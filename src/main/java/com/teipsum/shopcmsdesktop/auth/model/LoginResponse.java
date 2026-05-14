package com.teipsum.shopcmsdesktop.auth.model;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String role
){}
