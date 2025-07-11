package com.salon.Controller;

import com.salon.Model.Customers.LoyaltyCustomer;
import com.salon.Model.Customers.LoyaltyCustomerDAO;
import com.salon.Model.DatabaseConnection;
import com.salon.Utils.CustomAlert;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class SmsSenderController {

    @FXML private Button sendButton;
    @FXML private TextField numberTxt;
    @FXML private TextArea massageTxt;
    @FXML private RadioButton selectAllRadio;

    // ✅ API credentials from SMSlenz
    private final String USER_ID = "75";
    private final String API_KEY = "13ca3f2f-c58e-45c4-b985-b41483e5a81c";
    private final String SENDER_ID = "MAGICAL";

    @FXML
    private void toggleAllContacts() {
        numberTxt.setDisable(selectAllRadio.isSelected());
    }

    @FXML
    private void sendSms() {
        String messageTemplate = massageTxt.getText().trim();

        if (messageTemplate.isEmpty()) {
            CustomAlert.showAlert("Input Error", "Please enter a message to send.");
            return;
        }

        if (selectAllRadio.isSelected()) {
            try (Connection conn = DatabaseConnection.connect()) {
                LoyaltyCustomerDAO dao = new LoyaltyCustomerDAO(conn);
                List<LoyaltyCustomer> customers = dao.getAllCustomers();

                for (LoyaltyCustomer customer : customers) {
                    try {
                        String number = formatPhoneNumber(customer.getPhone());
                        String personalizedMessage = "Dear " + customer.getName() + ", " + messageTemplate + " - Salon Magical";
                        sendMessageToNumber(number, personalizedMessage);
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Skipping invalid number: " + customer.getPhone());
                    }
                }

                CustomAlert.showSuccess("Messages sent to all valid customers!");
                massageTxt.clear();

            } catch (Exception e) {
                CustomAlert.showAlert("Error", "Error sending messages: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            String number = numberTxt.getText().trim();
            if (number.isEmpty()) {
                CustomAlert.showAlert("Input Error", "Please enter a phone number.");
                return;
            }

            try {
                String formattedNumber = formatPhoneNumber(number);
                String message = "Dear Customer, " + messageTemplate + " - Salon Magical";

                boolean success = sendMessageToNumber(formattedNumber, message);
                if (success) {
                    CustomAlert.showSuccess("Message sent successfully!");
                    numberTxt.clear();
                    massageTxt.clear();
                } else {
                    CustomAlert.showAlert("Failed", "Failed to send the message. Try again.");
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

            int responseCode = conn.getResponseCode();
            InputStream responseStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            try (Scanner scanner = new Scanner(responseStream)) {
                scanner.useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                System.out.println("SMS API Response: " + response);
                return response.toLowerCase().contains("success");
            }

        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            return false;
        }
    }

    /**
     * Converts a valid Sri Lankan phone number to international format
     */
    private String formatPhoneNumber(String raw) {
        String cleaned = raw.replaceAll("\\D", "");

        if (cleaned.length() == 10 && cleaned.startsWith("0")) {
            return "94" + cleaned.substring(1);
        } else if (cleaned.length() == 9) {
            return "94" + cleaned;
        } else if (cleaned.length() == 11 && cleaned.startsWith("94")) {
            return cleaned;
        } else {
            throw new IllegalArgumentException("Invalid Sri Lankan phone number: " + raw);
        }
    }
}
