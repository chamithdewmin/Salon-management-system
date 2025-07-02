package com.salon.Utils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    // Regex for Sri Lankan mobile numbers: 10 digits starting with 070,071,072,...078
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(070|071|072|074|075|076|077|078)\\d{7}$");

    // Standard email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Validates a Sri Lankan phone number.
     * @param phoneNumber Phone number as string.
     * @return true if valid, false otherwise.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * Validates an email address.
     * @param email Email as string.
     * @return true if valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Optional: Testing main method
    public static void main(String[] args) {
        // Test phone numbers
        System.out.println(isValidPhoneNumber("0771234567")); // true
        System.out.println(isValidPhoneNumber("0791234567")); // false

        // Test emails
        System.out.println(isValidEmail("example@gmail.com")); // true
        System.out.println(isValidEmail("invalid@")); // false
    }
}
