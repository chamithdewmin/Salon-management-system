package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Views.ViewOption;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class StaffMenuController {

    @FXML
    private Button dashboardButton, AddRateButton, smsBtn, staffBtn, logoutBtn;

    private List<Button> menuButtons;

    @FXML
    private void initialize() {
        // Add buttons to list
        menuButtons = List.of(dashboardButton, AddRateButton, smsBtn, staffBtn, logoutBtn);

        // Ensure all buttons have the common style class
        for (Button btn : menuButtons) {
            if (!btn.getStyleClass().contains("sidebar-button")) {
                btn.getStyleClass().add("sidebar-button");
            }
        }

        // Set up actions with highlighting
        dashboardButton.setOnAction(e -> {
            setActiveButton(dashboardButton);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD);
        });

        AddRateButton.setOnAction(e -> {
            setActiveButton(AddRateButton);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.ADD_RATE);
        });

        smsBtn.setOnAction(e -> {
            setActiveButton(smsBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.SMS);
        });

        staffBtn.setOnAction(e -> {
            setActiveButton(staffBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.STAFF);
        });

        logoutBtn.setOnAction(e -> {
            setActiveButton(logoutBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.LOGOUT);
        });

        // Set default
        setActiveButton(dashboardButton);
    }

    private void setActiveButton(Button selectedButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("selected");
        }
        selectedButton.getStyleClass().add("selected");
    }
}
