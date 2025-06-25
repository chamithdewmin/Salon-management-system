package com.salon.Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SmsService {

    private static final String USER_ID = "334";
    private static final String API_KEY = "b99a552d-f5da-47c4-9fae-5fb3db8cf090";
    private static final String SENDER_ID = "SMSlenzDEMO";

    public static boolean sendSms(String number, String message) {
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
}
