package com.teipsum.shopcmsdesktop.product.model.enums;

public enum ProductCategory {
    TOPS("Tops"),
    BOTTOMS("Bottoms"),
    DRESSES_SKIRTS("Dresses & Skirts"),
    OUTERWEAR("Outerwear"),
    UNDERWEAR_SLEEPWEAR("Underwear & Sleepwear"),
    ACTIVEWEAR("Activewear"),
    SWIMWEAR("Swimwear"),
    SHOES("Shoes"),
    ACCESSORIES("Accessories"),
    BAGS("Bags"),
    JEWELRY("Jewelry"),
    KIDS("Kids"),
    BABY("Baby");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}