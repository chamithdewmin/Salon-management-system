package com.salon.Controller;

import com.salon.Model.Model;
import com.salon.Utils.CustomAlert;
import com.salon.Views.ViewOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuFullController implements Initializable {

    @FXML
    private BorderPane menuParent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Listen for menu option changes and update center view
        Model.getInstance().getViewFactory().getMenuItem().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case DASHBOARD -> menuParent.setCenter(Model.getInstance().getViewFactory().showDashboardView());
                case ADD_RATE -> menuParent.setCenter(Model.getInstance().getViewFactory().showIncomeExpensesView());
                case REPORT -> menuParent.setCenter(Model.getInstance().getViewFactory().showSettingView());
                case TASK -> menuParent.setCenter(Model.getInstance().getViewFactory().showTask());
                case SMS -> menuParent.setCenter(Model.getInstance().getViewFactory().showSms());
                case CUSTOMER -> menuParent.setCenter(Model.getInstance().getViewFactory().customer());
                case EXPENSES -> menuParent.setCenter(Model.getInstance().getViewFactory().showExpenses());
                case ADDSTAFF -> menuParent.setCenter(Model.getInstance().getViewFactory().AddStaffShow());
                case STAFF -> menuParent.setCenter(Model.getInstance().getViewFactory().showStaff());
                case LOGOUT -> {
                    boolean confirm = CustomAlert.showConfirmation("Logout", "Are you sure you want to logout?");
                    if (!confirm) return;

                    // Show login window again
                    Model.getInstance().getViewFactory().showLoginWindow();

                    // Close the current main window
                    Stage currentStage = (Stage) menuParent.getScene().getWindow();
                    currentStage.close();
                }
            }
        });

        // Set default menu view when app opens
        Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD);
    }
}
