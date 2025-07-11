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

public class StaffMenuFullController implements Initializable {

    @FXML
    private BorderPane menuParent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getViewFactory().getMenuItem().addListener((obs, oldVal, newVal) -> {
            switch (newVal) {
                case DASHBOARD -> menuParent.setCenter(Model.getInstance().getViewFactory().showDashboardView());
                case ADD_RATE -> menuParent.setCenter(Model.getInstance().getViewFactory().showIncomeExpensesView());
                case SMS -> menuParent.setCenter(Model.getInstance().getViewFactory().showSms());
                case STAFF -> menuParent.setCenter(Model.getInstance().getViewFactory().showStaff());
                case LOGOUT -> {
                    boolean confirm = CustomAlert.showConfirmation("Logout", "Are you sure you want to logout?");
                    if (!confirm) return;

                    // Show login window
                    Model.getInstance().getViewFactory().showLoginWindow();

                    // Close current stage (window)
                    Stage currentStage = (Stage) menuParent.getScene().getWindow();
                    currentStage.close();
                }
            }
        });

        // Set default view
        Model.getInstance().getViewFactory().getMenuItem().set(ViewOption.DASHBOARD);
    }
}
