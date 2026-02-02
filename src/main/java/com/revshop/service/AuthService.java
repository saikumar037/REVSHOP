package com.revshop.service;

import com.revshop.dao.UserDAO;
import com.revshop.dao.SellerDAO;
import com.revshop.model.User;
import com.revshop.model.Buyer;
import com.revshop.model.Seller;
import com.revshop.util.PasswordUtil;
import com.revshop.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

import static com.revshop.util.ConsoleColors.*;

public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private UserDAO userDAO;
    private SellerDAO sellerDAO;
    private User currentUser;
    private Scanner scanner;

    public AuthService(Scanner scanner) {
        this.userDAO = new UserDAO();
        this.sellerDAO = new SellerDAO();
        this.scanner = scanner;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isBuyer() {
        return currentUser != null && currentUser.getUserType() == User.UserType.BUYER;
    }

    public boolean isSeller() {
        return currentUser != null && currentUser.getUserType() == User.UserType.SELLER;
    }

    // Register buyer
    public boolean registerBuyer() {
        System.out.println(header("Buyer Registration"));

        System.out.print(inputPrompt("First Name: "));
        String firstName = scanner.nextLine().trim();

        System.out.print(inputPrompt("Last Name: "));
        String lastName = scanner.nextLine().trim();

        System.out.print(inputPrompt("Email: "));
        String email = scanner.nextLine().trim();

        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println(error("Invalid email format!"));
            return false;
        }

        // Check if email exists
        if (userDAO.getUserByEmail(email) != null) {
            System.out.println(error("Email already registered!"));
            return false;
        }

        System.out.print(inputPrompt("Password (min 8 chars with letters and numbers): "));
        String password = scanner.nextLine();

        if (!ValidationUtil.isValidPassword(password)) {
            System.out.println(error("Password must be at least 8 characters with letters and numbers!"));
            return false;
        }

        System.out.print(inputPrompt("Confirm Password: "));
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println(error("Passwords do not match!"));
            return false;
        }

        System.out.print(inputPrompt("Phone: "));
        String phone = scanner.nextLine().trim();

        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println(error("Invalid phone number! Must be 10 digits."));
            return false;
        }

        System.out.print(inputPrompt("Address: "));
        String address = scanner.nextLine().trim();

        // Security question
        System.out.print(inputPrompt("Security Question (e.g., What is your pet's name?): "));
        String securityQuestion = scanner.nextLine().trim();

        System.out.print(inputPrompt("Answer: "));
        String securityAnswer = scanner.nextLine().trim();

        // Create buyer
        Buyer buyer = new Buyer(email, password, firstName, lastName, phone, address);
        buyer.setSecurityQuestion(securityQuestion);
        buyer.setSecurityAnswer(securityAnswer);
        buyer.setPasswordHint(PasswordUtil.generatePasswordHint(password));

        if (userDAO.createUser(buyer)) {
            logger.info("Buyer registered: {}", email);
            return true;
        } else {
            System.out.println(error("Registration failed!"));
            return false;
        }
    }

    // Register seller
    public boolean registerSeller() {
        System.out.println(header("Seller Registration"));

        System.out.print(inputPrompt("First Name: "));
        String firstName = scanner.nextLine().trim();

        System.out.print(inputPrompt("Last Name: "));
        String lastName = scanner.nextLine().trim();

        System.out.print(inputPrompt("Email: "));
        String email = scanner.nextLine().trim();

        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println(error("Invalid email format!"));
            return false;
        }

        // Check if email exists
        if (userDAO.getUserByEmail(email) != null) {
            System.out.println(error("Email already registered!"));
            return false;
        }

        System.out.print(inputPrompt("Password (min 8 chars with letters and numbers): "));
        String password = scanner.nextLine();

        if (!ValidationUtil.isValidPassword(password)) {
            System.out.println(error("Password must be at least 8 characters with letters and numbers!"));
            return false;
        }

        System.out.print(inputPrompt("Confirm Password: "));
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println(error("Passwords do not match!"));
            return false;
        }

        System.out.print(inputPrompt("Phone: "));
        String phone = scanner.nextLine().trim();

        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println(error("Invalid phone number! Must be 10 digits."));
            return false;
        }

        System.out.print(inputPrompt("Address: "));
        String address = scanner.nextLine().trim();

        System.out.print(inputPrompt("Business Name: "));
        String businessName = scanner.nextLine().trim();

        System.out.print(inputPrompt("Business Address: "));
        String businessAddress = scanner.nextLine().trim();

        System.out.print(inputPrompt("Tax ID: "));
        String taxId = scanner.nextLine().trim();

        System.out.print(inputPrompt("Business Phone: "));
        String businessPhone = scanner.nextLine().trim();

        // Security question
        System.out.print(inputPrompt("Security Question (e.g., What is your pet's name?): "));
        String securityQuestion = scanner.nextLine().trim();

        System.out.print(inputPrompt("Answer: "));
        String securityAnswer = scanner.nextLine().trim();

        // Create seller
        Seller seller = new Seller(email, password, firstName, lastName, phone, address,
                businessName, businessAddress, taxId, businessPhone);
        seller.setSecurityQuestion(securityQuestion);
        seller.setSecurityAnswer(securityAnswer);
        seller.setPasswordHint(PasswordUtil.generatePasswordHint(password));

        if (sellerDAO.createSeller(seller)) {
            logger.info("Seller registered: {}", email);
            return true;
        } else {
            System.out.println(error("Registration failed!"));
            return false;
        }
    }

    // Login - FIXED METHOD
    public boolean login() {
        System.out.println(header("Login"));

        System.out.print(inputPrompt("Email: "));
        String email = scanner.nextLine().trim();

        System.out.print(inputPrompt("Password: "));
        String password = scanner.nextLine();

        User user = userDAO.authenticateUser(email, password);

        if (user != null) {
            currentUser = user;
            System.out.println(success("Welcome, " + user.getFirstName() + "!"));
            logger.info("User logged in: {}", email);

            // Load seller details if seller
            if (user.getUserType() == User.UserType.SELLER) {
                // Use getSellerById() instead of getSellerByUserId()
                Seller seller = sellerDAO.getSellerById(user.getUserId());
                if (seller != null) {
                    currentUser = seller;
                }
            }
            return true;
        } else {
            System.out.println(error("Invalid email or password!"));
            return false;
        }
    }

    // Logout
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getEmail());
        }
        currentUser = null;
    }

    // Change password
    public boolean changePassword() {
        if (!isLoggedIn()) {
            System.out.println(error("You must be logged in to change password!"));
            return false;
        }

        System.out.println(header("Change Password"));

        System.out.print(inputPrompt("Current Password: "));
        String currentPassword = scanner.nextLine();

        // Verify current password
        if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getPassword())) {
            System.out.println(error("Current password is incorrect!"));
            return false;
        }

        System.out.print(inputPrompt("New Password: "));
        String newPassword = scanner.nextLine();

        if (!ValidationUtil.isValidPassword(newPassword)) {
            System.out.println(error("Password must be at least 8 characters with letters and numbers!"));
            return false;
        }

        System.out.print(inputPrompt("Confirm New Password: "));
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println(error("Passwords do not match!"));
            return false;
        }

        if (userDAO.changePassword(currentUser.getUserId(), newPassword)) {
            currentUser.setPassword(PasswordUtil.hashPassword(newPassword));
            return true;
        } else {
            System.out.println(error("Failed to change password!"));
            return false;
        }
    }

    // Forgot password
    public boolean forgotPassword() {
        System.out.println(header("Forgot Password"));

        System.out.print(inputPrompt("Enter your email: "));
        String email = scanner.nextLine().trim();

        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            System.out.println(error("Email not found!"));
            return false;
        }

        System.out.println(info("Security Question: " + user.getSecurityQuestion()));
        System.out.print(inputPrompt("Answer: "));
        String answer = scanner.nextLine().trim();

        System.out.print(inputPrompt("New Password: "));
        String newPassword = scanner.nextLine();

        if (!ValidationUtil.isValidPassword(newPassword)) {
            System.out.println(error("Password must be at least 8 characters with letters and numbers!"));
            return false;
        }

        if (userDAO.resetPassword(email, answer, newPassword)) {
            return true;
        } else {
            System.out.println(error("Security answer is incorrect!"));
            return false;
        }
    }

    // Get password hint
    public void getPasswordHint() {
        System.out.println(header("Password Hint"));

        System.out.print(inputPrompt("Enter your email: "));
        String email = scanner.nextLine().trim();

        String hint = userDAO.getPasswordHint(email);
        if (hint != null) {
            System.out.println(info("Password Hint: " + hint));
        } else {
            System.out.println(error("No password hint found for this email."));
        }
    }

    // Update profile
    public boolean updateProfile() {
        if (!isLoggedIn()) {
            System.out.println(error("You must be logged in to update profile!"));
            return false;
        }

        System.out.println(header("Update Profile"));

        System.out.print(inputPrompt("First Name (" + currentUser.getFirstName() + "): "));
        String firstName = scanner.nextLine().trim();
        if (!firstName.isEmpty()) {
            currentUser.setFirstName(firstName);
        }

        System.out.print(inputPrompt("Last Name (" + currentUser.getLastName() + "): "));
        String lastName = scanner.nextLine().trim();
        if (!lastName.isEmpty()) {
            currentUser.setLastName(lastName);
        }

        System.out.print(inputPrompt("Phone (" + currentUser.getPhone() + "): "));
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) {
            if (!ValidationUtil.isValidPhone(phone)) {
                System.out.println(error("Invalid phone number! Must be 10 digits."));
                return false;
            }
            currentUser.setPhone(phone);
        }

        System.out.print(inputPrompt("Address (" + currentUser.getAddress() + "): "));
        String address = scanner.nextLine().trim();
        if (!address.isEmpty()) {
            currentUser.setAddress(address);
        }

        if (userDAO.updateUser(currentUser)) {
            return true;
        } else {
            System.out.println(error("Failed to update profile!"));
            return false;
        }
    }

    // For sellers to update business info
    public boolean updateBusinessInfo() {
        if (!isSeller()) {
            System.out.println(error("Only sellers can update business information!"));
            return false;
        }

        Seller seller = (Seller) currentUser;

        System.out.println(header("Update Business Information"));

        System.out.print(inputPrompt("Business Name (" + seller.getBusinessName() + "): "));
        String businessName = scanner.nextLine().trim();
        if (!businessName.isEmpty()) {
            seller.setBusinessName(businessName);
        }

        System.out.print(inputPrompt("Business Address (" + seller.getBusinessAddress() + "): "));
        String businessAddress = scanner.nextLine().trim();
        if (!businessAddress.isEmpty()) {
            seller.setBusinessAddress(businessAddress);
        }

        System.out.print(inputPrompt("Tax ID (" + seller.getTaxId() + "): "));
        String taxId = scanner.nextLine().trim();
        if (!taxId.isEmpty()) {
            seller.setTaxId(taxId);
        }

        System.out.print(inputPrompt("Business Phone (" + seller.getBusinessPhone() + "): "));
        String businessPhone = scanner.nextLine().trim();
        if (!businessPhone.isEmpty()) {
            seller.setBusinessPhone(businessPhone);
        }

        if (sellerDAO.updateSeller(seller)) {
            return true;
        } else {
            System.out.println(error("Failed to update business information!"));
            return false;
        }
    }
}