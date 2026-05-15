package com.teipsum.shopcmsdesktop.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.teipsum.shopcmsdesktop.auth.TokenStore;
import com.teipsum.shopcmsdesktop.config.AppConfig;
import com.teipsum.shopcmsdesktop.product.model.Product;
import com.teipsum.shopcmsdesktop.product.model.ProductPage;
import com.teipsum.shopcmsdesktop.product.model.enums.Gender;
import com.teipsum.shopcmsdesktop.product.model.enums.ProductCategory;
import com.teipsum.shopcmsdesktop.product.model.enums.ProductSubcategory;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class ProductService {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
            .build();

    private static final String PRODUCTS_PATH = "/api/admin/products";

    public static Product createProduct(
            String title,
            String description,
            BigDecimal price,
            BigDecimal discount,
            ProductCategory category,
            ProductSubcategory subcategory,
            Gender gender,
            List<String> sizes,
            boolean available,
            List<File> imageFiles
    ) throws Exception {

        String boundary = "----JavaFXBoundary" + System.currentTimeMillis();

        StringBuilder productJson = new StringBuilder("{");
        productJson.append("\"title\":\"").append(escapeJson(title)).append("\",");
        productJson.append("\"description\":\"").append(escapeJson(description)).append("\",");
        productJson.append("\"price\":").append(price).append(",");
        if (discount != null) {
            productJson.append("\"discount\":").append(discount).append(",");
        }
        productJson.append("\"category\":\"").append(category.name()).append("\",");
        if (subcategory != null) {
            productJson.append("\"subcategory\":\"").append(subcategory.name()).append("\",");
        }
        if (gender != null) {
            productJson.append("\"gender\":\"").append(gender.name()).append("\",");
        }
        productJson.append("\"sizes\":[");
        for (int i = 0; i < sizes.size(); i++) {
            productJson.append("\"").append(sizes.get(i)).append("\"");
            if (i < sizes.size() - 1) productJson.append(",");
        }
        productJson.append("],");
        productJson.append("\"available\":").append(available);
        productJson.append("}");

        var bodyBuilder = new java.io.ByteArrayOutputStream();

        String productPart = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"product\"\r\n" +
                "Content-Type: application/json\r\n\r\n" +
                productJson + "\r\n";
        bodyBuilder.write(productPart.getBytes());

        if (imageFiles != null) {
            for (File file : imageFiles) {
                String imagePart = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"images\"; filename=\""
                        + file.getName() + "\"\r\n" +
                        "Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n\r\n";
                bodyBuilder.write(imagePart.getBytes());
                bodyBuilder.write(Files.readAllBytes(file.toPath()));
                bodyBuilder.write("\r\n".getBytes());
            }
        }

        bodyBuilder.write(("--" + boundary + "--\r\n").getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + PRODUCTS_PATH))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + TokenStore.getAccessToken())
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBuilder.toByteArray()))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return parseProduct(response.body());
        } else if (response.statusCode() == 401) {
            throw new SecurityException("Unauthorized — please login again");
        } else if (response.statusCode() == 403) {
            throw new SecurityException("Forbidden — admin role required");
        } else {
            throw new RuntimeException("Failed to create product: " + response.statusCode()
                    + " — " + response.body());
        }
    }

    public static ProductPage getProducts(int page, int size) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + PRODUCTS_PATH
                        + "?page=" + page + "&size=" + size))
                .timeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
                .header("Authorization", "Bearer " + TokenStore.getAccessToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseProductPage(response.body());
        } else {
            throw new RuntimeException("Failed to fetch products: " + response.statusCode());
        }
    }

    public static void deleteProduct(UUID id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.getApiBaseUrl() + PRODUCTS_PATH + "/" + id))
                .timeout(Duration.ofSeconds(AppConfig.getApiTimeoutSeconds()))
                .header("Authorization", "Bearer " + TokenStore.getAccessToken())
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete product: " + response.statusCode());
        }
    }

    private static Product parseProduct(String json) throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper.readValue(json, Product.class);
    }

    private static ProductPage parseProductPage(String json) throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper.readValue(json, ProductPage.class);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}