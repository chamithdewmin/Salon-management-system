package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Views.ViewOption;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;

public class MenuController {

    public Button dashboardButton;
    public Button AddRateButton;
    public Button SettingButton;
    public Button taskBtn;
    public Button smsBtn;
    public Button customerBtn;
    public Button staffBtn;
    public Button logoutBtn;
    public Button addExpaenceBtn;
    public Button addStaff;

    private List<Button> menuButtons;

    @FXML
    private void initialize() {
        // Add all sidebar buttons to the list
        menuButtons = List.of(
                dashboardButton, AddRateButton, SettingButton, taskBtn,
                smsBtn, customerBtn, staffBtn, addExpaenceBtn, addStaff, logoutBtn
        );

        // Assign style class
        for (Button btn : menuButtons) {
            if (!btn.getStyleClass().contains("sidebar-button")) {
                btn.getStyleClass().add("sidebar-button");
            }
        }

        // Add action handlers
        dashboardButton.setOnAction(e -> {
            setActiveButton(dashboardButton);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD);
        });

        AddRateButton.setOnAction(e -> {
            setActiveButton(AddRateButton);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.ADD_RATE);
        });

        SettingButton.setOnAction(e -> {
            setActiveButton(SettingButton);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.REPORT);
        });

        taskBtn.setOnAction(e -> {
            setActiveButton(taskBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.TASK);
        });

        smsBtn.setOnAction(e -> {
            setActiveButton(smsBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.SMS);
        });

        customerBtn.setOnAction(e -> {
            setActiveButton(customerBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.CUSTOMER);
        });

        staffBtn.setOnAction(e -> {
            setActiveButton(staffBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.STAFF);
        });

        addExpaenceBtn.setOnAction(e -> {
            setActiveButton(addExpaenceBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.EXPENSES);
        });

        addStaff.setOnAction(e -> {
            setActiveButton(addStaff);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.ADDSTAFF);
        });

        logoutBtn.setOnAction(e -> {
            setActiveButton(logoutBtn);
            Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.LOGOUT);
        });

        // Set default selected
        setActiveButton(dashboardButton);
    }

    private void setActiveButton(Button selectedButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("selected");
        }
        selectedButton.getStyleClass().add("selected");
    }
}
