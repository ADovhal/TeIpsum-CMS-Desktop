package com.teipsum.shopcmsdesktop.product.controller;

import com.teipsum.shopcmsdesktop.product.model.Product;
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
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductEditController {

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
    @FXML private VBox dropZone;
    @FXML private Label dropZoneLabel;
    @FXML private CheckBox availableCheck;
    @FXML private Button submitButton;

    private final List<String> selectedSizes    = new ArrayList<>();
    private final List<File>   selectedImages   = new ArrayList<>();
    private final List<Button> standardSizeButtons = new ArrayList<>();
    private final List<Button> customSizeButtons   = new ArrayList<>();

    private Product currentProduct;

    private static final List<String> STANDARD_SIZES =
            List.of("XS", "S", "M", "L", "XL", "XXL", "XXXL",
                    "28", "30", "32", "34", "36", "38", "40");

    public void setProduct(Product product) {
        this.currentProduct = product;
        populateForm(product);
    }

    @FXML
    public void initialize() {
        categoryCombo.getItems().addAll(ProductCategory.values());
        genderCombo.getItems().addAll(Gender.values());
        setupDropZone();
        createSizeButtonsOnce();
    }

    private void populateForm(Product product) {
        titleField.setText(product.title() != null ? product.title() : "");
        descriptionField.setText(product.description() != null ? product.description() : "");
        priceField.setText(product.price() != null ? product.price().toPlainString() : "");
        discountField.setText(product.discount() != null ? product.discount().toPlainString() : "");
        availableCheck.setSelected(product.available());

        if (product.category() != null) {
            categoryCombo.setValue(product.category());
            handleCategoryChange();
        }
        if (product.subcategory() != null) {
            subcategoryCombo.setValue(product.subcategory());
        }
        if (product.gender() != null) {
            genderCombo.setValue(product.gender());
        }
        if (product.sizes() != null) {
            selectedSizes.addAll(product.sizes());
        }
        if (product.imageUrls() != null && !product.imageUrls().isEmpty()) {
            renderExistingImagePreviews(product.imageUrls());
        }
        updateSizeButtonsState();
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
            updateSizeButtonsState();
        }
    }

    @FXML
    private void handleUploadImages() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Product Images");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images (jpg, png, gif)",
                        "*.jpg", "*.jpeg", "*.png", "*.gif")
        );
        List<File> files = chooser.showOpenMultipleDialog(submitButton.getScene().getWindow());
        if (files != null) {
            for (File file : files) {
                if (!selectedImages.contains(file)) selectedImages.add(file);
            }
            renderImagePreviews();
        }
    }

    @FXML
    private void handleSubmit() {
        clearErrors();
        if (!validate()) return;

        submitButton.setDisable(true);
        submitButton.setText("Saving...");

        String title           = titleField.getText().trim();
        String description     = descriptionField.getText().trim();
        BigDecimal price       = new BigDecimal(priceField.getText().trim());
        BigDecimal discount    = discountField.getText().trim().isEmpty()
                ? null : new BigDecimal(discountField.getText().trim());
        ProductCategory    category    = categoryCombo.getValue();
        ProductSubcategory subcategory = subcategoryCombo.getValue();
        Gender             gender      = genderCombo.getValue();
        boolean            available   = availableCheck.isSelected();

        Thread.ofVirtual().start(() -> {
            try {
                ProductService.updateProduct(
                        currentProduct.id(),
                        title, description, price, discount,
                        category, subcategory, gender,
                        selectedSizes, available, selectedImages
                );
                Platform.runLater(() -> showSuccess("Product updated successfully!"));
            } catch (SecurityException e) {
                Platform.runLater(() -> showError(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to update product: " + e.getMessage()));
            } finally {
                Platform.runLater(() -> {
                    submitButton.setDisable(false);
                    submitButton.setText("Save Changes");
                });
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/com/teipsum/shopcmsdesktop/MainWindow.fxml")
                    )
            );
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Product Management");
        } catch (Exception e) {
            showError("Failed to navigate back");
        }
    }

    private void createSizeButtonsOnce() {
        for (String size : STANDARD_SIZES) {
            Button chip = new Button(size);
            chip.setFocusTraversable(false);
            chip.setOnAction(e -> {
                if (selectedSizes.contains(size)) selectedSizes.remove(size);
                else selectedSizes.add(size);
                updateSizeButtonsState();
            });
            standardSizeButtons.add(chip);
            sizesBox.getChildren().add(chip);
        }
    }

    private void updateSizeButtonsState() {
        for (Button chip : standardSizeButtons) {
            boolean selected = selectedSizes.contains(chip.getText());
            chip.getStyleClass().setAll(selected ? "size-chip-selected" : "size-chip");
        }
        for (Button btn : customSizeButtons) sizesBox.getChildren().remove(btn);
        customSizeButtons.clear();
        for (String size : selectedSizes) {
            if (!STANDARD_SIZES.contains(size)) {
                Button chip = new Button(size + " X");
                chip.getStyleClass().setAll("size-chip-selected");
                chip.setFocusTraversable(false);
                chip.setOnAction(e -> { selectedSizes.remove(size); updateSizeButtonsState(); });
                customSizeButtons.add(chip);
                sizesBox.getChildren().add(chip);
            }
        }
    }

    private void setupDropZone() {
        dropZone.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });
        dropZone.setOnDragEntered(event -> {
            dropZone.getStyleClass().add("drop-zone-active");
            dropZoneLabel.setText("Drop images here!");
            event.consume();
        });
        dropZone.setOnDragExited(event -> {
            dropZone.getStyleClass().remove("drop-zone-active");
            dropZoneLabel.setText("Drag & drop images here or click Choose Images");
            event.consume();
        });
        dropZone.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    if (isImageFile(file) && !selectedImages.contains(file)) {
                        selectedImages.add(file);
                        success = true;
                    }
                }
                if (success) renderImagePreviews();
            }
            event.setDropCompleted(success);
            event.consume();
            dropZone.getStyleClass().remove("drop-zone-active");
            dropZoneLabel.setText("Drag & drop images here or click Choose Images");
        });
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".png") || name.endsWith(".gif");
    }

    private void renderImagePreviews() {
        imagePreviewBox.getChildren().clear();
        for (int i = 0; i < selectedImages.size(); i++) {
            File file = selectedImages.get(i);
            final int index = i;
            try {
                if (!file.exists() || !file.isFile()) continue;

                BufferedImage buffered = ImageIO.read(file);
                if (buffered == null) {
                    System.err.println("Could not load image: " + file.getName());
                    continue;
                }
                Image image = javafx.embed.swing.SwingFXUtils.toFXImage(buffered, null);

                StackPane container = new StackPane();
                container.getStyleClass().add("image-preview-container");

                ImageView iv = new ImageView(image);
                iv.setFitWidth(100);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);
                iv.getStyleClass().add("image-preview");

                Button removeBtn = new Button("X");
                removeBtn.getStyleClass().add("image-remove-btn");
                removeBtn.setFocusTraversable(false);
                removeBtn.setOnAction(e -> { selectedImages.remove(index); renderImagePreviews(); });

                container.getChildren().addAll(iv, removeBtn);
                imagePreviewBox.getChildren().add(container);
            } catch (Exception e) {
                System.err.println("Exception loading image " + file.getName() + ": " + e.getMessage());
            }
        }
        dropZoneLabel.setText(selectedImages.isEmpty()
                ? "Drag & drop images here or click Choose Images"
                : selectedImages.size() + " image(s) selected");
    }

    private void renderExistingImagePreviews(List<String> urls) {
        for (String url : urls) {
            Thread.ofVirtual().start(() -> {
                try {
                    java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                    java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create(url))
                            .build();
                    byte[] bytes = client.send(request,
                            java.net.http.HttpResponse.BodyHandlers.ofByteArray()).body();

                    java.awt.image.BufferedImage buffered =
                            ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                    if (buffered == null) {
                        System.err.println("ImageIO returned null for: " + url);
                        return;
                    }
                    Image image = javafx.embed.swing.SwingFXUtils.toFXImage(buffered, null);

                    Platform.runLater(() -> {
                        StackPane container = new StackPane();
                        container.setMinSize(100, 100);
                        container.setPrefSize(100, 100);
                        container.getStyleClass().add("image-preview-container");

                        ImageView iv = new ImageView(image);
                        iv.setFitWidth(100);
                        iv.setFitHeight(100);
                        iv.setPreserveRatio(true);
                        iv.getStyleClass().add("image-preview");

                        Label existingLabel = new Label("existing");
                        existingLabel.setStyle("-fx-background-color:rgba(0,0,0,0.5);" +
                                "-fx-text-fill:white;-fx-font-size:9;-fx-padding:2 4;");
                        StackPane.setAlignment(existingLabel, javafx.geometry.Pos.BOTTOM_CENTER);

                        container.getChildren().addAll(iv, existingLabel);
                        imagePreviewBox.getChildren().add(container);
                    });
                } catch (Exception e) {
                    System.err.println("Could not load existing image: " + url + " — " + e.getMessage());
                }
            });
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (titleField.getText().trim().isEmpty()) {
            showFieldError(titleError, "Title is required"); valid = false;
        }
        if (priceField.getText().trim().isEmpty()) {
            showFieldError(priceError, "Price is required"); valid = false;
        } else {
            try {
                BigDecimal p = new BigDecimal(priceField.getText().trim());
                if (p.compareTo(BigDecimal.ZERO) <= 0) {
                    showFieldError(priceError, "Price must be positive"); valid = false;
                }
            } catch (NumberFormatException e) {
                showFieldError(priceError, "Invalid price format"); valid = false;
            }
        }
        if (categoryCombo.getValue() == null) {
            showFieldError(categoryError, "Category is required"); valid = false;
        }
        return valid;
    }

    private void showFieldError(Label label, String msg) {
        label.setText(msg); label.setVisible(true); label.setManaged(true);
    }

    private void showError(String msg) {
        errorBanner.setText("✖ " + msg);
        errorBanner.setVisible(true); errorBanner.setManaged(true);
        successBanner.setVisible(false); successBanner.setManaged(false);
    }

    private void showSuccess(String msg) {
        successBanner.setText("✔ " + msg);
        successBanner.setVisible(true); successBanner.setManaged(true);
        errorBanner.setVisible(false); errorBanner.setManaged(false);
    }

    private void clearErrors() {
        errorBanner.setVisible(false);   errorBanner.setManaged(false);
        successBanner.setVisible(false); successBanner.setManaged(false);
        titleError.setVisible(false);    titleError.setManaged(false);
        priceError.setVisible(false);    priceError.setManaged(false);
        categoryError.setVisible(false); categoryError.setManaged(false);
    }
}