package com.teipsum.shopcmsdesktop.admin.controller;

import com.teipsum.shopcmsdesktop.admin.model.AdminRegisterRequest;
import com.teipsum.shopcmsdesktop.admin.service.AdminService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Objects;

public class AdminController {

    @FXML private Button backButton;
    @FXML private Label errorBanner;
    @FXML private Label successBanner;
    @FXML private TextField nameField;
    @FXML private Label nameError;
    @FXML private TextField surnameField;
    @FXML private Label surnameError;
    @FXML private TextField phoneField;
    @FXML private Label phoneError;
    @FXML private DatePicker dobPicker;
    @FXML private Label dobError;
    @FXML private TextField emailField;
    @FXML private Label emailError;
    @FXML private PasswordField passwordField;
    @FXML private Label passwordError;
    @FXML private Button submitButton;

    @FXML
    public void initialize() {
        dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now().minusYears(18)));
            }
        });
    }

    @FXML
    private void handleSubmit() {
        clearErrors();
        if (!validate()) return;

        submitButton.setDisable(true);
        submitButton.setText("Creating...");

        AdminRegisterRequest request = new AdminRegisterRequest(
                emailField.getText().trim(),
                passwordField.getText(),
                nameField.getText().trim(),
                surnameField.getText().trim(),
                phoneField.getText().trim(),
                dobPicker.getValue()
        );

        Thread.ofVirtual().start(() -> {
            try {
                AdminService.registerAdmin(request);
                Platform.runLater(() -> {
                    showSuccess("Admin '" + request.email() + "' created successfully!");
                    clearForm();
                });
            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> showFieldError(emailError, e.getMessage()));
            } catch (SecurityException e) {
                Platform.runLater(() -> showError(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> showError(
                        "Connection error: " + e.getMessage()
                ));
            } finally {
                Platform.runLater(() -> {
                    submitButton.setDisable(false);
                    submitButton.setText("Create Admin");
                });
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/com/teipsum/shopcmsdesktop/MainWindow.fxml")
            ));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Product Management");
        } catch (Exception e) {
            showError("Failed to navigate back");
        }
    }

    private boolean validate() {
        boolean valid = true;

        if (nameField.getText().trim().isEmpty()) {
            showFieldError(nameError, "First name is required");
            valid = false;
        }
        if (surnameField.getText().trim().isEmpty()) {
            showFieldError(surnameError, "Last name is required");
            valid = false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showFieldError(phoneError, "Phone is required");
        } else if (!phoneField.getText().trim().matches("^\\+?[\\d\\s\\-()]{10,}$")) {
            showFieldError(phoneError, "Invalid phone format");
            valid = false;
        }
        if (dobPicker.getValue() == null) {
            showFieldError(dobError, "Date of birth is required");
            valid = false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showFieldError(emailError, "Email is required");
            valid = false;
        } else if (!emailField.getText().contains("@")) {
            showFieldError(emailError, "Invalid email format");
            valid = false;
        }
        if (passwordField.getText().isEmpty()) {
            showFieldError(passwordError, "Password is required");
            valid = false;
        } else if (passwordField.getText().length() < 8) {
            showFieldError(passwordError, "Password must be at least 8 characters");
            valid = false;
        }

        return valid;
    }

    private void clearForm() {
        nameField.clear();
        surnameField.clear();
        phoneField.clear();
        dobPicker.setValue(null);
        emailField.clear();
        passwordField.clear();
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
        errorBanner.setVisible(false);    errorBanner.setManaged(false);
        successBanner.setVisible(false);  successBanner.setManaged(false);
        nameError.setVisible(false);      nameError.setManaged(false);
        surnameError.setVisible(false);   surnameError.setManaged(false);
        phoneError.setVisible(false);     phoneError.setManaged(false);
        dobError.setVisible(false);       dobError.setManaged(false);
        emailError.setVisible(false);     emailError.setManaged(false);
        passwordError.setVisible(false);  passwordError.setManaged(false);
    }
}