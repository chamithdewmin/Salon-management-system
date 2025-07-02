package com.salon.Views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class ViewFactory {
    private final ObjectProperty<ViewOption> menuItem = new SimpleObjectProperty<>();
    private AnchorPane dashboardView;
    private AnchorPane incomeExpensesView;
    private AnchorPane settingView;
    private AnchorPane reminderView;
    private AnchorPane invoiceView;
    private AnchorPane taskView;
    private AnchorPane smsView;
    private AnchorPane customerView;
    private AnchorPane staffView;

    public ObjectProperty<ViewOption> getMenuItem() {
        return menuItem;
    }

    public AnchorPane showDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Xaml/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane showIncomeExpensesView() {
        if (incomeExpensesView == null) {
            try {
                incomeExpensesView = new FXMLLoader(getClass().getResource("/Xaml/Appointment.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return incomeExpensesView;
    }

    public AnchorPane showSettingView() {
        if (settingView == null) {
            try {
                settingView = new FXMLLoader(getClass().getResource("/Xaml/Report.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return settingView;
    }

    public AnchorPane showStaff() {
        if (staffView == null) {
            try {
                staffView = new FXMLLoader(getClass().getResource("/Xaml/Staff.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return staffView;
    }

    public AnchorPane showTask() {
        if (taskView == null) {
            try {
                taskView = new FXMLLoader(getClass().getResource("/Xaml/Service.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return taskView;
    }

    public AnchorPane customer() {
        if (customerView == null) {
            try {
                customerView = new FXMLLoader(getClass().getResource("/Xaml/customerManager.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return customerView;
    }

    public AnchorPane showSms() {
        if (smsView == null) {
            try {
                smsView = new FXMLLoader(getClass().getResource("/Xaml/SmsSender.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return smsView;
    }

    public AnchorPane showReminderView() {
        if (reminderView == null) {
            try {
                reminderView = new FXMLLoader(getClass().getResource("/Xaml/Reminder.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reminderView;
    }

    public AnchorPane showInvoiceView() {
        if (invoiceView == null) {
            try {
                invoiceView = new FXMLLoader(getClass().getResource("/Xaml/Invoice.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return invoiceView;
    }

    public void fullWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Xaml/MenuFull.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            // ✅ Set icon image - ensure logo.png is in /resources/images/
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));

            stage.setScene(scene);
            stage.setTitle("Salon Magical");
            stage.setResizable(true);
            stage.setMaximized(true); // use for get full screen when open the system
//            stage.setFullScreen(true);  // use for get full screen not task bar and no close buttons
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Xaml/Login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();

            // Set icon (optional, if you have a logo)
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}