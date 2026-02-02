package com.revshop.test;

import com.revshop.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void testEmailValidation() {
        // Valid emails - should return TRUE
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co"));
        assertTrue(ValidationUtil.isValidEmail("user@domain.com"));
        assertTrue(ValidationUtil.isValidEmail("user@domain.co.in"));
        assertTrue(ValidationUtil.isValidEmail("user123@domain.com"));

        // Invalid emails - should return FALSE
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));  // No @
        assertFalse(ValidationUtil.isValidEmail("user@.com"));      // No domain before .
        assertFalse(ValidationUtil.isValidEmail("@domain.com"));    // No username
        assertFalse(ValidationUtil.isValidEmail("user@domain."));   // No TLD
        assertFalse(ValidationUtil.isValidEmail(""));               // Empty
        assertFalse(ValidationUtil.isValidEmail(null));             // Null
        assertFalse(ValidationUtil.isValidEmail("user@com"));       // No . in domain
        assertFalse(ValidationUtil.isValidEmail("user@.com."));     // Trailing dot
    }

    @Test
    void testPasswordValidation() {
        // Valid passwords - should return TRUE
        assertTrue(ValidationUtil.isValidPassword("Password123"));
        assertTrue(ValidationUtil.isValidPassword("pass1234"));
        assertTrue(ValidationUtil.isValidPassword("1234abcd"));
        assertTrue(ValidationUtil.isValidPassword("ABCD1234"));
        assertTrue(ValidationUtil.isValidPassword("a1b2c3d4e5")); // 10 chars

        // Invalid passwords - should return FALSE
        assertFalse(ValidationUtil.isValidPassword("short"));      // Too short
        assertFalse(ValidationUtil.isValidPassword("1234567"));    // 7 chars, no letter
        assertFalse(ValidationUtil.isValidPassword("abcdefg"));    // 7 chars, no digit
        assertFalse(ValidationUtil.isValidPassword(""));           // Empty
        assertFalse(ValidationUtil.isValidPassword(null));         // Null
        assertFalse(ValidationUtil.isValidPassword("12345678"));   // Only digits
        assertFalse(ValidationUtil.isValidPassword("abcdefgh"));   // Only letters
    }

    @Test
    void testPhoneValidation() {
        // Valid phones - should return TRUE
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertTrue(ValidationUtil.isValidPhone("0000000000"));

        // Invalid phones - should return FALSE
        assertFalse(ValidationUtil.isValidPhone("12345"));         // Too short
        assertFalse(ValidationUtil.isValidPhone("abcdefghij"));    // Not digits
        assertFalse(ValidationUtil.isValidPhone("123-456-7890"));  // Has dashes
        assertFalse(ValidationUtil.isValidPhone("123 456 7890"));  // Has spaces
        assertFalse(ValidationUtil.isValidPhone(""));              // Empty
        assertFalse(ValidationUtil.isValidPhone(null));            // Null
        assertFalse(ValidationUtil.isValidPhone("12345678901"));   // Too long
    }

    @Test
    void testNameValidation() {
        // Valid names - should return TRUE
        assertTrue(ValidationUtil.isValidName("John"));
        assertTrue(ValidationUtil.isValidName("Mary"));
        assertTrue(ValidationUtil.isValidName("Alice"));
        assertTrue(ValidationUtil.isValidName("Bob"));
        assertTrue(ValidationUtil.isValidName("Jo"));

        // Invalid names - should return FALSE
        assertFalse(ValidationUtil.isValidName(""));       // Empty
        assertFalse(ValidationUtil.isValidName(" "));      // Only space
        assertFalse(ValidationUtil.isValidName(null));     // Null
        assertFalse(ValidationUtil.isValidName("A"));      // Too short
        assertFalse(ValidationUtil.isValidName("  "));     // Only spaces
    }
}