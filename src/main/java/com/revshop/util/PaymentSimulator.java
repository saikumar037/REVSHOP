package com.revshop.util;

import java.util.Random;

import static com.revshop.util.ConsoleColors.*;

public class PaymentSimulator {
    private static final Random random = new Random();

    public static boolean processPayment(double amount, String paymentMethod) {
        System.out.println();
        System.out.println(header("Payment Processing"));
        System.out.printf(price("Amount: $%.2f%n"), amount);
        System.out.println(info("Payment Method: " + paymentMethod));

        // Simulate payment processing delay
        try {
            System.out.print(info("Processing"));
            for (int i = 0; i < 3; i++) {
                System.out.print(info("."));
                Thread.sleep(500);
            }
            System.out.println();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate 90% success rate
        boolean success = random.nextDouble() < 0.9;

        if (success) {
            System.out.println(success("Payment processed successfully! ✅"));
            return true;
        } else {
            System.out.println(error("Payment failed. Please try again. ❌"));
            return false;
        }
    }

    public static String[] getAvailablePaymentMethods() {
        return new String[] {
                "Credit Card 💳",
                "Debit Card 💳",
                "PayPal 📱",
                "Bank Transfer 🏦",
                "Cash on Delivery 💰"
        };
    }

    public static String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + random.nextInt(1000);
    }
}