package com.teipsum.shopcmsdesktop.product.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teipsum.shopcmsdesktop.product.model.enums.Gender;
import com.teipsum.shopcmsdesktop.product.model.enums.ProductCategory;
import com.teipsum.shopcmsdesktop.product.model.enums.ProductSubcategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(
        UUID id,
        String sku,
        String title,
        String description,
        BigDecimal price,
        BigDecimal discount,
        ProductCategory category,
        ProductSubcategory subcategory,
        Gender gender,
        List<String> imageUrls,
        List<String> sizes,
        boolean available
) {}