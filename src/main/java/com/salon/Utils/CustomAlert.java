package com.salon.Utils;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CustomAlert {

    // Show a simple information alert
    public static void showAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    // Show alert with custom AlertType
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyStyleAndIcon(alert, "/images/logo.png");
        alert.showAndWait();
    }

    // Show a confirmation dialog
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        applyStyleAndIcon(alert, "/images/logo.png");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // Show a success dialog with tick image
    public static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            ImageView imageView = new ImageView(new Image(CustomAlert.class.getResourceAsStream("/images/success.png")));
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            alert.setGraphic(imageView);
        } catch (Exception e) {
            System.err.println("Success icon not found: " + e.getMessage());
        }

        alert.getButtonTypes().add(ButtonType.OK);
        applyStyleAndIcon(alert, "/images/logo.png");
        alert.showAndWait();
    }

    // 🔹 Central method to apply CSS + window icon
    private static void applyStyleAndIcon(Alert alert, String iconPath) {
        try {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            if (stage.getIcons().isEmpty() && iconPath != null) {
                Image icon = new Image(CustomAlert.class.getResourceAsStream(iconPath));
                stage.getIcons().add(icon);
            }

            String css = CustomAlert.class.getResource("/CSS/alert-style.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);

        } catch (Exception e) {
            System.err.println("Failed to apply icon or CSS: " + e.getMessage());
        }
    }
}
