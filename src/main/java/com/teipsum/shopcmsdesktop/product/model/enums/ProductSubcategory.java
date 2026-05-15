package com.teipsum.shopcmsdesktop.product.model.enums;

public enum ProductSubcategory {
    // TOPS
    T_SHIRTS("T-Shirts", ProductCategory.TOPS),
    SHIRTS("Shirts", ProductCategory.TOPS),
    BLOUSES("Blouses", ProductCategory.TOPS),
    TANK_TOPS("Tank Tops", ProductCategory.TOPS),
    HOODIES("Hoodies", ProductCategory.TOPS),
    SWEATERS("Sweaters", ProductCategory.TOPS),
    CARDIGANS("Cardigans", ProductCategory.TOPS),
    CROP_TOPS("Crop Tops", ProductCategory.TOPS),
    POLO_SHIRTS("Polo Shirts", ProductCategory.TOPS),
    // BOTTOMS
    JEANS("Jeans", ProductCategory.BOTTOMS),
    PANTS("Pants", ProductCategory.BOTTOMS),
    SHORTS("Shorts", ProductCategory.BOTTOMS),
    LEGGINGS("Leggings", ProductCategory.BOTTOMS),
    JOGGERS("Joggers", ProductCategory.BOTTOMS),
    CHINOS("Chinos", ProductCategory.BOTTOMS),
    CARGO_PANTS("Cargo Pants", ProductCategory.BOTTOMS),
    // DRESSES & SKIRTS
    CASUAL_DRESSES("Casual Dresses", ProductCategory.DRESSES_SKIRTS),
    FORMAL_DRESSES("Formal Dresses", ProductCategory.DRESSES_SKIRTS),
    EVENING_DRESSES("Evening Dresses", ProductCategory.DRESSES_SKIRTS),
    MAXI_DRESSES("Maxi Dresses", ProductCategory.DRESSES_SKIRTS),
    MINI_SKIRTS("Mini Skirts", ProductCategory.DRESSES_SKIRTS),
    MIDI_SKIRTS("Midi Skirts", ProductCategory.DRESSES_SKIRTS),
    MAXI_SKIRTS("Maxi Skirts", ProductCategory.DRESSES_SKIRTS),
    // OUTERWEAR
    COATS("Coats", ProductCategory.OUTERWEAR),
    JACKETS("Jackets", ProductCategory.OUTERWEAR),
    BLAZERS("Blazers", ProductCategory.OUTERWEAR),
    VESTS("Vests", ProductCategory.OUTERWEAR),
    PARKAS("Parkas", ProductCategory.OUTERWEAR),
    BOMBER_JACKETS("Bomber Jackets", ProductCategory.OUTERWEAR),
    LEATHER_JACKETS("Leather Jackets", ProductCategory.OUTERWEAR),
    // UNDERWEAR & SLEEPWEAR
    BRAS("Bras", ProductCategory.UNDERWEAR_SLEEPWEAR),
    PANTIES("Panties", ProductCategory.UNDERWEAR_SLEEPWEAR),
    BOXERS("Boxers", ProductCategory.UNDERWEAR_SLEEPWEAR),
    BRIEFS("Briefs", ProductCategory.UNDERWEAR_SLEEPWEAR),
    PAJAMAS("Pajamas", ProductCategory.UNDERWEAR_SLEEPWEAR),
    NIGHTGOWNS("Nightgowns", ProductCategory.UNDERWEAR_SLEEPWEAR),
    ROBES("Robes", ProductCategory.UNDERWEAR_SLEEPWEAR),
    LOUNGEWEAR("Loungewear", ProductCategory.UNDERWEAR_SLEEPWEAR),
    // ACTIVEWEAR
    SPORTS_TOPS("Sports Tops", ProductCategory.ACTIVEWEAR),
    SPORTS_BOTTOMS("Sports Bottoms", ProductCategory.ACTIVEWEAR),
    TRACKSUITS("Tracksuits", ProductCategory.ACTIVEWEAR),
    YOGA_WEAR("Yoga Wear", ProductCategory.ACTIVEWEAR),
    GYM_WEAR("Gym Wear", ProductCategory.ACTIVEWEAR),
    RUNNING_GEAR("Running Gear", ProductCategory.ACTIVEWEAR),
    // SWIMWEAR
    BIKINIS("Bikinis", ProductCategory.SWIMWEAR),
    ONE_PIECE("One Piece", ProductCategory.SWIMWEAR),
    SWIM_TRUNKS("Swim Trunks", ProductCategory.SWIMWEAR),
    BOARD_SHORTS("Board Shorts", ProductCategory.SWIMWEAR),
    COVER_UPS("Cover Ups", ProductCategory.SWIMWEAR),
    // SHOES
    SNEAKERS("Sneakers", ProductCategory.SHOES),
    BOOTS("Boots", ProductCategory.SHOES),
    SANDALS("Sandals", ProductCategory.SHOES),
    HIGH_HEELS("High Heels", ProductCategory.SHOES),
    FLATS("Flats", ProductCategory.SHOES),
    DRESS_SHOES("Dress Shoes", ProductCategory.SHOES),
    ATHLETIC_SHOES("Athletic Shoes", ProductCategory.SHOES),
    LOAFERS("Loafers", ProductCategory.SHOES),
    // ACCESSORIES
    BELTS("Belts", ProductCategory.ACCESSORIES),
    HATS("Hats", ProductCategory.ACCESSORIES),
    CAPS("Caps", ProductCategory.ACCESSORIES),
    SCARVES("Scarves", ProductCategory.ACCESSORIES),
    GLOVES("Gloves", ProductCategory.ACCESSORIES),
    SUNGLASSES("Sunglasses", ProductCategory.ACCESSORIES),
    WATCHES("Watches", ProductCategory.ACCESSORIES),
    TIES("Ties", ProductCategory.ACCESSORIES),
    // BAGS
    HANDBAGS("Handbags", ProductCategory.BAGS),
    BACKPACKS("Backpacks", ProductCategory.BAGS),
    TOTE_BAGS("Tote Bags", ProductCategory.BAGS),
    CROSSBODY_BAGS("Crossbody Bags", ProductCategory.BAGS),
    CLUTCHES("Clutches", ProductCategory.BAGS),
    WALLETS("Wallets", ProductCategory.BAGS),
    BRIEFCASES("Briefcases", ProductCategory.BAGS),
    // JEWELRY
    NECKLACES("Necklaces", ProductCategory.JEWELRY),
    EARRINGS("Earrings", ProductCategory.JEWELRY),
    BRACELETS("Bracelets", ProductCategory.JEWELRY),
    RINGS("Rings", ProductCategory.JEWELRY),
    BROOCHES("Brooches", ProductCategory.JEWELRY),
    // KIDS
    BOYS_TOPS("Boys' Tops", ProductCategory.KIDS),
    BOYS_BOTTOMS("Boys' Bottoms", ProductCategory.KIDS),
    BOYS_OUTERWEAR("Boys' Outerwear", ProductCategory.KIDS),
    GIRLS_TOPS("Girls' Tops", ProductCategory.KIDS),
    GIRLS_BOTTOMS("Girls' Bottoms", ProductCategory.KIDS),
    GIRLS_DRESSES("Girls' Dresses", ProductCategory.KIDS),
    GIRLS_OUTERWEAR("Girls' Outerwear", ProductCategory.KIDS),
    KIDS_SHOES("Kids' Shoes", ProductCategory.KIDS),
    KIDS_ACCESSORIES("Kids' Accessories", ProductCategory.KIDS),
    // BABY
    BABY_BODYSUITS("Baby Bodysuits", ProductCategory.BABY),
    BABY_SLEEPWEAR("Baby Sleepwear", ProductCategory.BABY),
    BABY_OUTERWEAR("Baby Outerwear", ProductCategory.BABY),
    BABY_SHOES("Baby Shoes", ProductCategory.BABY),
    BABY_ACCESSORIES("Baby Accessories", ProductCategory.BABY);

    private final String displayName;
    private final ProductCategory parentCategory;

    ProductSubcategory(String displayName, ProductCategory parentCategory) {
        this.displayName = displayName;
        this.parentCategory = parentCategory;
    }

    public ProductCategory getParentCategory() { return parentCategory; }

    @Override
    public String toString() { return displayName; }
}