package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Views.ViewOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuFullController implements Initializable {

    @FXML
    private BorderPane menuParent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getViewFactory().getMenuItem().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case DASHBOARD -> menuParent.setCenter(Model.getInstance().getViewFactory().showDashboardView());
                case ADD_RATE -> menuParent.setCenter(Model.getInstance().getViewFactory().showIncomeExpensesView());
                case REPORT -> menuParent.setCenter(Model.getInstance().getViewFactory().showSettingView());
                case TASK -> menuParent.setCenter(Model.getInstance().getViewFactory().showTask());
                case SMS -> menuParent.setCenter(Model.getInstance().getViewFactory().showSms());
                case CUSTOMER -> menuParent.setCenter(Model.getInstance().getViewFactory().customer());
                case STAFF -> menuParent.setCenter(Model.getInstance().getViewFactory().showStaff());
            }
        });

        // Set default view
        Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD);
    }
}