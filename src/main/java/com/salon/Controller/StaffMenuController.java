package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Views.ViewOption;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StaffMenuController {


    public Button smsBtn;
    public Button customerBtn;
    public Button staffBtn;
    public Button logoutBtn;
    @FXML
    private Button dashboardButton;

    @FXML
    private Button AddRateButton;

    @FXML
    private void initialize() {
        dashboardButton.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD));
        AddRateButton.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.ADD_RATE));
        smsBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.SMS));
        staffBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.STAFF));
        logoutBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.LOGOUT));
    }
}