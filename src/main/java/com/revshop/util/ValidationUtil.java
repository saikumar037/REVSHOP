package com.revshop.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Email must have: username@domain.tld
        // domain must have at least one dot
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
            // If we found both, we can break early
            if (hasLetter && hasDigit) break;
        }

        return hasLetter && hasDigit;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove any spaces or dashes
        String cleanedPhone = phone.trim().replaceAll("", "");
        // Must be exactly 10 digits
        return cleanedPhone.matches("\\d{10}");
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }

    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    // Helper method for checking empty strings
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // Business validation
    public static boolean isValidBusinessName(String name) {
        return isNotEmpty(name) && name.trim().length() >= 3;
    }

    // Address validation
    public static boolean isValidAddress(String address) {
        return isNotEmpty(address) && address.trim().length() >= 10;
    }

    // MRP should be greater than or equal to price
    public static boolean isValidMRP(double mrp, double price) {
        return mrp >= price;
    }

    // Discount price should be less than MRP
    public static boolean isValidDiscount(double discount, double mrp) {
        return discount > 0 && discount < mrp;
    }
}