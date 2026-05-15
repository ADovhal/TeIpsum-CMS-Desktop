package com.teipsum.shopcmsdesktop.product.model.enums;

public enum Gender {
    MEN("Men"),
    WOMEN("Women"),
    BOYS("Boys"),
    GIRLS("Girls"),
    BABY_BOY("Baby Boy"),
    BABY_GIRL("Baby Girl"),
    UNISEX("Unisex");

    private final String displayName;

    Gender(String displayName) { this.displayName = displayName; }

    @Override
    public String toString() { return displayName; }
}