package com.teipsum.shopcmsdesktop.product.controller;

import com.teipsum.shopcmsdesktop.product.model.enums.Gender;
import com.teipsum.shopcmsdesktop.product.model.enums.ProductCategory;
import com.teipsum.shopcmsdesktop.product.model.enums.ProductSubcategory;
import com.teipsum.shopcmsdesktop.product.service.ProductService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductController {

    @FXML private Button backButton;
    @FXML private Label errorBanner;
    @FXML private Label successBanner;
    @FXML private TextField titleField;
    @FXML private Label titleError;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private Label priceError;
    @FXML private TextField discountField;
    @FXML private ComboBox<ProductCategory> categoryCombo;
    @FXML private Label categoryError;
    @FXML private ComboBox<ProductSubcategory> subcategoryCombo;
    @FXML private ComboBox<Gender> genderCombo;
    @FXML private FlowPane sizesBox;
    @FXML private TextField customSizeField;
    @FXML private FlowPane imagePreviewBox;
    @FXML private CheckBox availableCheck;
    @FXML private Button submitButton;

    private final List<String> selectedSizes = new ArrayList<>();
    private final List<File> selectedImages = new ArrayList<>();

    private static final List<String> STANDARD_SIZES =
            List.of("XS", "S", "M", "L", "XL", "XXL", "XXXL",
                    "28", "30", "32", "34", "36", "38", "40");

    @FXML
    public void initialize() {
        categoryCombo.getItems().addAll(ProductCategory.values());
        genderCombo.getItems().addAll(Gender.values());
        renderSizeChips();
    }

    @FXML
    private void handleCategoryChange() {
        ProductCategory selected = categoryCombo.getValue();
        subcategoryCombo.getItems().clear();
        if (selected != null) {
            for (ProductSubcategory sub : ProductSubcategory.values()) {
                if (sub.getParentCategory() == selected) {
                    subcategoryCombo.getItems().add(sub);
                }
            }
        }
    }

    @FXML
    private void handleAddCustomSize() {
        String size = customSizeField.getText().trim().toUpperCase();
        if (!size.isEmpty() && !selectedSizes.contains(size)) {
            selectedSizes.add(size);
            customSizeField.clear();
            renderSizeChips();
        }
    }

    @FXML
    private void handleUploadImages() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Product Images");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images",
                        "*.jpg", "*.jpeg", "*.png", "*.webp")
        );
        List<File> files = chooser.showOpenMultipleDialog(
                submitButton.getScene().getWindow()
        );
        if (files != null) {
            selectedImages.addAll(files);
            renderImagePreviews();
        }
    }

    @FXML
    private void handleSubmit() {
        clearErrors();
        if (!validate()) return;

        submitButton.setDisable(true);
        submitButton.setText("Creating...");

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        BigDecimal price = new BigDecimal(priceField.getText().trim());
        BigDecimal discount = discountField.getText().trim().isEmpty()
                ? null : new BigDecimal(discountField.getText().trim());
        ProductCategory category = categoryCombo.getValue();
        ProductSubcategory subcategory = subcategoryCombo.getValue();
        Gender gender = genderCombo.getValue();
        boolean available = availableCheck.isSelected();

        Thread.ofVirtual().start(() -> {
            try {
                ProductService.createProduct(
                        title, description, price, discount,
                        category, subcategory, gender,
                        selectedSizes, available, selectedImages
                );
                Platform.runLater(() -> {
                    showSuccess("Product created successfully!");
                    clearForm();
                });
            } catch (SecurityException e) {
                Platform.runLater(() -> showError(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to create product: "
                        + e.getMessage()));
            } finally {
                Platform.runLater(() -> {
                    submitButton.setDisable(false);
                    submitButton.setText("Create Product");
                });
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/fxml/MainWindow.fxml")
                    )
            );
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Product Management");
        } catch (Exception e) {
            showError("Failed to navigate back");
        }
    }

    private void renderSizeChips() {
        sizesBox.getChildren().clear();
        for (String size : STANDARD_SIZES) {
            Button chip = new Button(size);
            boolean isSelected = selectedSizes.contains(size);
            chip.getStyleClass().add(isSelected ? "size-chip-selected" : "size-chip");
            chip.setOnAction(e -> {
                if (selectedSizes.contains(size)) {
                    selectedSizes.remove(size);
                } else {
                    selectedSizes.add(size);
                }
                renderSizeChips();
            });
            sizesBox.getChildren().add(chip);
        }

        for (String size : selectedSizes) {
            if (!STANDARD_SIZES.contains(size)) {
                Button chip = new Button(size + " ✕");
                chip.getStyleClass().add("size-chip-selected");
                chip.setOnAction(e -> {
                    selectedSizes.remove(size);
                    renderSizeChips();
                });
                sizesBox.getChildren().add(chip);
            }
        }
    }

    private void renderImagePreviews() {
        imagePreviewBox.getChildren().clear();
        for (File file : selectedImages) {
            try {
                ImageView iv = new ImageView(
                        new Image(file.toURI().toString(), 80, 80, true, true)
                );
                iv.getStyleClass().add("image-preview");
                imagePreviewBox.getChildren().add(iv);
            } catch (Exception ignored) {}
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (titleField.getText().trim().isEmpty()) {
            showFieldError(titleError, "Title is required");
            valid = false;
        }
        if (priceField.getText().trim().isEmpty()) {
            showFieldError(priceError, "Price is required");
            valid = false;
        } else {
            try {
                BigDecimal p = new BigDecimal(priceField.getText().trim());
                if (p.compareTo(BigDecimal.ZERO) <= 0) {
                    showFieldError(priceError, "Price must be positive");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                showFieldError(priceError, "Invalid price format");
                valid = false;
            }
        }
        if (categoryCombo.getValue() == null) {
            showFieldError(categoryError, "Category is required");
            valid = false;
        }
        return valid;
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        discountField.clear();
        categoryCombo.setValue(null);
        subcategoryCombo.getItems().clear();
        genderCombo.setValue(null);
        selectedSizes.clear();
        selectedImages.clear();
        availableCheck.setSelected(true);
        renderSizeChips();
        imagePreviewBox.getChildren().clear();
    }

    private void showFieldError(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void showError(String msg) {
        errorBanner.setText("❌ " + msg);
        errorBanner.setVisible(true);
        errorBanner.setManaged(true);
        successBanner.setVisible(false);
        successBanner.setManaged(false);
    }

    private void showSuccess(String msg) {
        successBanner.setText("✅ " + msg);
        successBanner.setVisible(true);
        successBanner.setManaged(true);
        errorBanner.setVisible(false);
        errorBanner.setManaged(false);
    }

    private void clearErrors() {
        errorBanner.setVisible(false);   errorBanner.setManaged(false);
        successBanner.setVisible(false); successBanner.setManaged(false);
        titleError.setVisible(false);    titleError.setManaged(false);
        priceError.setVisible(false);    priceError.setManaged(false);
        categoryError.setVisible(false); categoryError.setManaged(false);
    }
}
