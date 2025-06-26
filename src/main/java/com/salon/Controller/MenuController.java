package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Views.ViewOption;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {


    public Button taskBtn;
    public Button smsBtn;
    public Button customerBtn;
    public Button inevtroyBtn;
    @FXML
    private Button dashboardButton;

    @FXML
    private Button AddRateButton;

    @FXML
    private Button SettingButton;

    @FXML
    private void initialize() {
        dashboardButton.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD));
        AddRateButton.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.ADD_RATE));
        SettingButton.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.REPORT));
        taskBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.TASK));
        smsBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.SMS));
        customerBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.CUSTOMER));
        inevtroyBtn.setOnAction(e -> Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.INVENTORY));
    }
}