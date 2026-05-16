package com.teipsum.shopcmsdesktop.ui;

import com.teipsum.shopcmsdesktop.auth.service.AuthService;
import com.teipsum.shopcmsdesktop.product.controller.ProductEditController;
import com.teipsum.shopcmsdesktop.product.model.Product;
import com.teipsum.shopcmsdesktop.product.model.ProductPage;
import com.teipsum.shopcmsdesktop.product.service.ProductService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainWindow {

    @FXML private TableView<Product>        productsTable;
    @FXML private TableColumn<Product, String>  colTitle;
    @FXML private TableColumn<Product, String>  colCategory;
    @FXML private TableColumn<Product, String>  colGender;
    @FXML private TableColumn<Product, String>  colPrice;
    @FXML private TableColumn<Product, String>  colDiscount;
    @FXML private TableColumn<Product, String>  colAvail;
    @FXML private TableColumn<Product, Void>    colActions;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label  pageLabel;
    @FXML private Label  errorBanner;
    @FXML private Label  successBanner;

    private int currentPage = 0;
    private int totalPages  = 1;
    private static final int PAGE_SIZE = 12;

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().title()));
        colCategory.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().category() != null ? data.getValue().category().name() : ""));
        colGender.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().gender() != null ? data.getValue().gender().name() : ""));
        colPrice.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().price() != null ? data.getValue().price().toPlainString() : ""));
        colDiscount.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().discount() != null ? data.getValue().discount().toPlainString() : "-"));
        colAvail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().available() ? "✔" : "✖"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("✏ Edit");
            private final Button deleteBtn = new Button("🗑 Delete");
            private final HBox   box       = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;-fx-background-radius:6;");
                deleteBtn.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:6;");

                editBtn.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    openEditView(p);
                });

                deleteBtn.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    confirmAndDelete(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        loadProducts();
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
    }

    @FXML
    private void handlePrev() {
        if (currentPage > 0) {
            currentPage--;
            loadProducts();
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadProducts();
        }
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        navigateTo(event,
                "/com/teipsum/shopcmsdesktop/product/ProductCreateView.fxml",
                "Shop CMS — Add Product");
    }

    @FXML
    private void handleAddAdmin(ActionEvent event) {
        navigateTo(event,
                "/com/teipsum/shopcmsdesktop/admin/AdminCreateView.fxml",
                "Shop CMS — Create Admin");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        AuthService.logout();
        navigateTo(event,
                "/com/teipsum/shopcmsdesktop/login/LoginView.fxml",
                "Shop CMS — Login");
    }

    private void loadProducts() {
        productsTable.setPlaceholder(new Label("Loading..."));
        productsTable.getItems().clear();
        prevButton.setDisable(true);
        nextButton.setDisable(true);

        Thread.ofVirtual().start(() -> {
            try {
                ProductPage page = ProductService.getProducts(currentPage, PAGE_SIZE);
                totalPages = page.totalPages();

                Platform.runLater(() -> {
                    productsTable.getItems().setAll(page.content());
                    productsTable.setPlaceholder(new Label("No products found"));
                    pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);
                    prevButton.setDisable(currentPage == 0);
                    nextButton.setDisable(currentPage >= totalPages - 1);
                    hideMessages();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load products: " + e.getMessage()));
            }
        });
    }

    private void openEditView(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource(
                            "/com/teipsum/shopcmsdesktop/product/ProductEditView.fxml")
            ));
            Parent root = loader.load();

            ProductEditController editController = loader.getController();
            editController.setProduct(product);

            Stage stage = (Stage) productsTable.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Edit Product");
        } catch (Exception e) {
            showError("Failed to open edit form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void confirmAndDelete(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Delete \"" + product.title() + "\"?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                Thread.ofVirtual().start(() -> {
                    try {
                        ProductService.deleteProduct(product.id());
                        Platform.runLater(() -> {
                            showSuccess("Product deleted successfully.");
                            loadProducts();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() ->
                                showError("Failed to delete: " + e.getMessage()));
                    }
                });
            }
        });
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle(title);
        } catch (Exception e) {
            showError("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorBanner.setText("✖ " + msg);
        errorBanner.setVisible(true);
        errorBanner.setManaged(true);
        successBanner.setVisible(false);
        successBanner.setManaged(false);
    }

    private void showSuccess(String msg) {
        successBanner.setText("✔ " + msg);
        successBanner.setVisible(true);
        successBanner.setManaged(true);
        errorBanner.setVisible(false);
        errorBanner.setManaged(false);
    }

    private void hideMessages() {
        errorBanner.setVisible(false);   errorBanner.setManaged(false);
        successBanner.setVisible(false); successBanner.setManaged(false);
    }
}