package com.salon.Controller;

import com.salon.Model.Customer;
import com.salon.Model.CustomerDAO;
import com.salon.Model.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

public class SmsSenderController {

    @FXML private TextField numberTxt;
    @FXML private TextArea massageTxt;
    @FXML private RadioButton selectAllRadio;

    private final String USER_ID = "334";
    private final String API_KEY = "b99a552d-f5da-47c4-9fae-5fb3db8cf090";
    private final String SENDER_ID = "SMSlenzDEMO";

    @FXML
    private void toggleAllContacts() {
        numberTxt.setDisable(selectAllRadio.isSelected());
    }

    @FXML
    private void sendSms() {
        String messageTemplate = massageTxt.getText().trim();

        if (messageTemplate.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a message.");
            return;
        }

        if (selectAllRadio.isSelected()) {
            try (Connection conn = DatabaseConnection.connect()) {
                CustomerDAO dao = new CustomerDAO(conn);
                List<Customer> customers = dao.getAllCustomers();

                for (Customer customer : customers) {
                    String personalizedMessage = String.format("Dear Customer, %s - Salon Magical", messageTemplate);
                    sendMessageToNumber(customer.getPhone(), personalizedMessage);
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Messages sent to all customers!");
                massageTxt.clear();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error sending messages: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            String number = numberTxt.getText().trim();
            if (number.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter the phone number.");
                return;
            }

            String message = String.format("Dear Customer, %s - Salon Magical", messageTemplate);

            boolean success = sendMessageToNumber(number, message);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Message sent successfully!");
                numberTxt.clear();
                massageTxt.clear();
            }
        }
    }

    private boolean sendMessageToNumber(String number, String message) {
        try {
            String params = String.format("user_id=%s&api_key=%s&sender_id=%s&contact=%s&message=%s",
                    URLEncoder.encode(USER_ID, "UTF-8"),
                    URLEncoder.encode(API_KEY, "UTF-8"),
                    URLEncoder.encode(SENDER_ID, "UTF-8"),
                    URLEncoder.encode(number, "UTF-8"),
                    URLEncoder.encode(message, "UTF-8")
            );

            URL url = new URL("https://smslenz.lk/api/send-sms");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
