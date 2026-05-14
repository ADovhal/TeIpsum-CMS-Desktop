package com.teipsum.shopcmsdesktop.product.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductPage(
        List<Product> content,
        int totalPages,
        long totalElements,
        int number
) {}