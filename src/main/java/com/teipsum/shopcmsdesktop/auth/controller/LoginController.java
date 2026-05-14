package com.teipsum.shopcmsdesktop.auth.controller;

import com.teipsum.shopcmsdesktop.auth.service.AuthService;
import com.teipsum.shopcmsdesktop.config.AppConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label generalError;
    @FXML private Button loginButton;
    @FXML private Label apiUrlLabel;

    @FXML
    public void initialize() {
        apiUrlLabel.setText("API: " + AppConfig.getApiBaseUrl());
        passwordField.setOnAction(e -> handleLogin());
        emailField.setOnAction(e -> passwordField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        clearErrors();

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        boolean valid = true;
        if (email.isEmpty()) {
            showFieldError(emailError, "Email is required");
            valid = false;
        } else if (!email.contains("@")) {
            showFieldError(emailError, "Invalid email format");
            valid = false;
        }
        if (password.isEmpty()) {
            showFieldError(passwordError, "Password is required");
            valid = false;
        } else if (password.length() < 8) {
            showFieldError(passwordError, "Password must be at least 8 characters");
            valid = false;
        }
        if (!valid) return;

        loginButton.setDisable(true);
        loginButton.setText("Signing in...");

        Thread.ofVirtual().start(() -> {
            try {
                AuthService.login(email, password);
                Platform.runLater(this::navigateToMain);
            } catch (SecurityException e) {
                Platform.runLater(() -> showGeneralError(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> showGeneralError(
                        "Connection error. Check API URL in config.properties"
                ));
            } finally {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Sign In");
                });
            }
        });
    }

    private void navigateToMain() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/com/teipsum/shopcmsdesktop/MainWindow.fxml"))
            );
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Product Management");
        } catch (Exception e) {
            showGeneralError("Failed to load main window");
        }
    }

    private void showFieldError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void showGeneralError(String message) {
        generalError.setText(message);
        generalError.setVisible(true);
        generalError.setManaged(true);
    }

    private void clearErrors() {
        emailError.setVisible(false);    emailError.setManaged(false);
        passwordError.setVisible(false); passwordError.setManaged(false);
        generalError.setVisible(false);  generalError.setManaged(false);
    }
}