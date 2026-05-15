package com.teipsum.shopcmsdesktop.ui;

import com.teipsum.shopcmsdesktop.auth.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import javafx.event.ActionEvent;
import java.util.Objects;


public class MainWindow {

    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/com/teipsum/shopcmsdesktop/product/ProductCreateView.fxml")
                    )
            );
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Add Product");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            AuthService.logout();
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/com/teipsum/shopcmsdesktop/login/LoginView.fxml"
                            )
                    )
            );
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Shop CMS — Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
