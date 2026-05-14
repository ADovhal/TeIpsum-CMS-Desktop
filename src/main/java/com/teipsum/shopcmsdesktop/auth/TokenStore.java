package com.teipsum.shopcmsdesktop.auth;

public class TokenStore {
    private static String accessToken;
    private static String refreshToken;
    private static String role;

    public static void save(String access, String refresh, String userRole) {
        accessToken = access;
        refreshToken = refresh;
        role = userRole;
    }

    public static String getAccessToken()  { return accessToken; }
    public static String getRefreshToken() { return refreshToken; }
    public static String getRole()         { return role; }

    public static boolean isLoggedIn() { return accessToken != null; }

    public static void clear() {
        accessToken = null;
        refreshToken = null;
        role = null;
    }
}
