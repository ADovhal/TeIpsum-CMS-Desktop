package com.teipsum.shopcmsdesktop.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Path localConfig = Paths.get("config.local.properties");
        if (Files.exists(localConfig)) {
            try (InputStream in = Files.newInputStream(localConfig)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Warning: could not load config.local.properties");
            }
        }

        String envUrl = System.getenv("CMS_API_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            props.setProperty("api.base.url", envUrl);
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