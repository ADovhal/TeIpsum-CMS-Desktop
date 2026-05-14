package com.teipsum.shopcmsdesktop.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class
                .getResourceAsStream("/config.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getApiBaseUrl() {
        return props.getProperty("api.base.url", "http://localhost:8080");
    }

    public static int getApiTimeoutSeconds() {
        return Integer.parseInt(
                props.getProperty("api.timeout.seconds", "10")
        );
    }
}