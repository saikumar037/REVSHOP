package com.revshop.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hashedPassword);
    }

    public static String generatePasswordHint(String password) {
        if (password == null || password.length() < 3) {
            return "Password hint not available";
        }

        char firstChar = password.charAt(0);
        char lastChar = password.charAt(password.length() - 1);
        int length = password.length();

        return String.format("Starts with '%c', ends with '%c', %d characters long",
                firstChar, lastChar, length);
    }

    public static String generateSecurityQuestion(String question, String answer) {
        // Simple implementation - in real app, would store separately
        return question;
    }
}