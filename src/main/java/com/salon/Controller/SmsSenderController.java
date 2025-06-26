package com.salon.Controller;

import com.salon.Model.Customers.Customer;
import com.salon.Model.Customers.CustomerDAO;
import com.salon.Model.DatabaseConnection;
import com.salon.Utils.CustomAlert;
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

    public Button sendButton;
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
            CustomAlert.showAlert("Input Error", "Please enter a message.");
            return;
        }

        if (selectAllRadio.isSelected()) {
            try (Connection conn = DatabaseConnection.connect()) {
                CustomerDAO dao = new CustomerDAO(conn);
                List<Customer> customers = dao.getAllCustomers();

                for (Customer customer : customers) {
                    String number = formatPhoneNumber(customer.getPhone());
                    String personalizedMessage = String.format("Dear Customer, %s - Salon Magical", messageTemplate);
                    sendMessageToNumber(number, personalizedMessage);
                }

                CustomAlert.showSuccess("Messages sent to all customers!");
                massageTxt.clear();
            } catch (Exception e) {
                CustomAlert.showAlert("Error", "Error sending messages: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            String number = numberTxt.getText().trim();
            if (number.isEmpty()) {
                CustomAlert.showAlert("Input Error", "Please enter the phone number.");
                return;
            }

            try {
                String formattedNumber = formatPhoneNumber(number);
                String message = String.format("Dear Customer, %s - Salon Magical", messageTemplate);

                boolean success = sendMessageToNumber(formattedNumber, message);
                if (success) {
                    CustomAlert.showSuccess("Message sent successfully!");
                    numberTxt.clear();
                    massageTxt.clear();
                } else {
                    CustomAlert.showAlert("Failed", "Failed to send the message.");
                }
            } catch (IllegalArgumentException ex) {
                CustomAlert.showAlert("Phone Number Error", ex.getMessage());
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

    /**
     * Converts any valid local SL number to international format: 94XXXXXXXXX
     */
    private String formatPhoneNumber(String raw) {
        String cleaned = raw.replaceAll("\\D", ""); // Remove non-digits

        if (cleaned.startsWith("0") && cleaned.length() == 10) {
            return "94" + cleaned.substring(1);
        } else if (cleaned.startsWith("94") && cleaned.length() == 11) {
            return cleaned;
        } else if (cleaned.length() == 9) {
            return "94" + cleaned;
        } else {
            throw new IllegalArgumentException("Invalid Sri Lankan phone number: " + raw);
        }
    }
}
