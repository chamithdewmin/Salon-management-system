package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Utils.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class LoginController {

    @FXML
    private TextField usernaemTxt;

    @FXML
    private PasswordField PasswordTxt;

    @FXML
    private Button LoginBtn;

    @FXML
    private CheckBox showPassword;

    private TextField visiblePassword;

    @FXML
    public void initialize() {
        setupShowPasswordToggle();

        LoginBtn.setOnAction(event -> handleLogin());

        usernaemTxt.setOnKeyPressed(this::handleEnterKey);
        PasswordTxt.setOnKeyPressed(this::handleEnterKey);
        visiblePassword.setOnKeyPressed(this::handleEnterKey);
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    private void handleLogin() {
        String username = usernaemTxt.getText();
        String password = showPassword.isSelected() ? visiblePassword.getText() : PasswordTxt.getText();

        if (username.isEmpty() || password.isEmpty()) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Error", "Please fill in both username and password.");
            return;
        }

        if (username.equals("admin") && password.equals("123")) {
            Model.getInstance().getViewFactory().fullWindow();
            LoginBtn.getScene().getWindow().hide();
        } else if (username.equals("user") && password.equals("123")) {
            Model.getInstance().getViewFactory().StafffullWindow();
            LoginBtn.getScene().getWindow().hide();
        } else {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            clearLoginFields();
        }
    }

    private void clearLoginFields() {
        usernaemTxt.clear();
        PasswordTxt.clear();
        visiblePassword.clear();
    }

    private void setupShowPasswordToggle() {
        visiblePassword = new TextField();
        visiblePassword.setLayoutX(PasswordTxt.getLayoutX());
        visiblePassword.setLayoutY(PasswordTxt.getLayoutY());
        visiblePassword.setPrefWidth(PasswordTxt.getPrefWidth());
        visiblePassword.setPrefHeight(PasswordTxt.getPrefHeight());
        visiblePassword.setPromptText("Password");
        visiblePassword.setVisible(false);
        visiblePassword.setManaged(false);

        visiblePassword.textProperty().bindBidirectional(PasswordTxt.textProperty());

        AnchorPane parent = (AnchorPane) PasswordTxt.getParent();
        parent.getChildren().add(visiblePassword);

        showPassword.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                visiblePassword.setVisible(true);
                visiblePassword.setManaged(true);
                PasswordTxt.setVisible(false);
                PasswordTxt.setManaged(false);
            } else {
                visiblePassword.setVisible(false);
                visiblePassword.setManaged(false);
                PasswordTxt.setVisible(true);
                PasswordTxt.setManaged(true);
            }
        });
    }
}
