package com.revshop.menu;

import com.revshop.service.AuthService;
import com.revshop.service.BuyerService;
import com.revshop.service.SellerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

import static com.revshop.util.ConsoleColors.*;

public class MainMenu {
    private static final Logger logger = LogManager.getLogger(MainMenu.class);
    private AuthService authService;
    private BuyerService buyerService;
    private SellerService sellerService;
    private Scanner scanner;
    private boolean running;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthService(scanner);
        this.running = true;
    }

    public void start() {
        logger.info("RevShop application started");
        showWelcomeBanner();

        while (running) {
            if (!authService.isLoggedIn()) {
                showMainMenu();
            } else if (authService.isBuyer()) {
                showBuyerMenu();
            } else if (authService.isSeller()) {
                showSellerMenu();
            }
        }

        scanner.close();
        logger.info("RevShop application stopped");
    }

    private void showWelcomeBanner() {
        System.out.println(GREEN_BOLD_BRIGHT + "╔══════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "║" + RESET + PURPLE_BOLD_BRIGHT + "                WELCOME TO REVSHOP                        " + RESET + GREEN_BOLD_BRIGHT + "║" + RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "║" + RESET + CYAN_BRIGHT + "           Your Console E-Commerce Platform               " + RESET + GREEN_BOLD_BRIGHT + "║" + RESET);
        System.out.println(GREEN_BOLD_BRIGHT + "╚══════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println(header("Welcome to RevShop"));
        System.out.println(option("1", "Register as Buyer"));
        System.out.println(option("2", "Register as Seller"));
        System.out.println(option("3", "Login"));
        System.out.println(option("4", "Forgot Password"));
        System.out.println(option("5", "Get Password Hint"));
        System.out.println(option("6", "Exit"));
        System.out.print(inputPrompt("Enter your choice: "));

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    if (authService.registerBuyer()) {
                        System.out.println(success("Registration successful! You can now login."));
                    }
                    break;
                case 2:
                    if (authService.registerSeller()) {
                        System.out.println(success("Seller registration successful! You can now login."));
                    }
                    break;
                case 3:
                    if (authService.login()) {
                        System.out.println(success("Login successful!"));
                    } else {
                        System.out.println(error("Invalid email or password!"));
                    }
                    break;
                case 4:
                    if (authService.forgotPassword()) {
                        System.out.println(success("Password reset successfully!"));
                    } else {
                        System.out.println(error("Password reset failed!"));
                    }
                    break;
                case 5:
                    authService.getPasswordHint();
                    break;
                case 6:
                    System.out.println(info("Thank you for using RevShop! Goodbye! 👋"));
                    running = false;
                    break;
                default:
                    System.out.println(error("Invalid choice! Please try again."));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input! Please enter a number."));
        }
        System.out.println();
    }

    private void showBuyerMenu() {
        if (buyerService == null) {
            buyerService = new BuyerService(scanner, authService.getCurrentUser().getUserId());
        }

        System.out.println(header("Buyer Dashboard"));
        System.out.println(subheader("Welcome, " + authService.getCurrentUser().getFirstName() + "! 👤"));
        System.out.println();

        System.out.println(subheader("📦 Products"));
        System.out.println(option("1", "Browse Products"));
        System.out.println(option("2", "View Product Details"));
        System.out.println(option("3", "Browse by Category"));
        System.out.println(option("4", "Search Products"));

        System.out.println();
        System.out.println(subheader("🛒 Shopping Cart"));
        System.out.println(option("5", "View Cart"));
        System.out.println(option("6", "Add to Cart"));
        System.out.println(option("7", "Update Cart Quantity"));
        System.out.println(option("8", "Remove from Cart"));
        System.out.println(option("9", "Checkout"));

        System.out.println();
        System.out.println(subheader("📋 Orders & Reviews"));
        System.out.println(option("10", "View Order History"));
        System.out.println(option("11", "Add Review"));

        System.out.println();
        System.out.println(subheader("❤️  Favorites"));
        System.out.println(option("12", "Manage Favorites"));

        System.out.println();
        System.out.println(subheader("🔔 Notifications"));
        System.out.println(option("13", "View Notifications"));
        System.out.println(option("14", "Mark Notification as Read"));

        System.out.println();
        System.out.println(subheader("👤 Account"));
        System.out.println(option("15", "Update Profile"));
        System.out.println(option("16", "Change Password"));
        System.out.println(option("17", "Logout"));

        System.out.print(inputPrompt("Enter your choice: "));

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.println(info("Browsing products..."));
                    buyerService.browseProducts();
                    break;
                case 2:
                    System.out.println(info("Viewing product details..."));
                    buyerService.viewProductDetails();
                    break;
                case 3:
                    System.out.println(info("Browsing by category..."));
                    buyerService.browseByCategory();
                    break;
                case 4:
                    System.out.println(search("Searching products..."));
                    buyerService.searchProducts();
                    break;
                case 5:
                    System.out.println(info("Viewing cart..."));
                    buyerService.viewCart();
                    break;
                case 6:
                    System.out.println(info("Adding to cart..."));
                    buyerService.addToCart();
                    break;
                case 7:
                    System.out.println(info("Updating cart quantity..."));
                    buyerService.updateCartQuantity();
                    break;
                case 8:
                    System.out.println(info("Removing from cart..."));
                    buyerService.removeFromCart();
                    break;
                case 9:
                    System.out.println(info("Processing checkout..."));
                    buyerService.checkout();
                    break;
                case 10:
                    System.out.println(info("Loading order history..."));
                    buyerService.viewOrderHistory();
                    break;
                case 11:
                    System.out.println(info("Adding review..."));
                    buyerService.addReview();
                    break;
                case 12:
                    System.out.println(info("Managing favorites..."));
                    showFavoritesMenu();
                    break;
                case 13:
                    System.out.println(info("Loading notifications..."));
                    buyerService.viewNotifications();
                    break;
                case 14:
                    System.out.println(info("Marking notification as read..."));
                    buyerService.markNotificationAsRead();
                    break;
                case 15:
                    System.out.println(info("Updating profile..."));
                    if (authService.updateProfile()) {
                        System.out.println(success("Profile updated successfully!"));
                    }
                    break;
                case 16:
                    System.out.println(info("Changing password..."));
                    if (authService.changePassword()) {
                        System.out.println(success("Password changed successfully!"));
                    }
                    break;
                case 17:
                    authService.logout();
                    buyerService = null;
                    System.out.println(success("Logged out successfully!"));
                    break;
                default:
                    System.out.println(error("Invalid choice! Please try again."));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input! Please enter a number."));
        }
        System.out.println();
    }

    private void showSellerMenu() {
        if (sellerService == null) {
            sellerService = new SellerService(scanner, authService.getCurrentUser().getUserId());
        }

        System.out.println(header("Seller Dashboard"));
        System.out.println(subheader("Welcome, " + authService.getCurrentUser().getFirstName() + "! 🏪"));
        System.out.println();

        System.out.println(subheader("📦 Product Management"));
        System.out.println(option("1", "Add Product"));
        System.out.println(option("2", "View Products"));
        System.out.println(option("3", "Update Product"));
        System.out.println(option("4", "Delete Product"));

        System.out.println();
        System.out.println(subheader("📋 Order Management"));
        System.out.println(option("5", "View Orders"));
        System.out.println(option("6", "Update Order Status"));

        System.out.println();
        System.out.println(subheader("📊 Inventory & Analytics"));
        System.out.println(option("7", "View Low Stock Alerts"));
        System.out.println(option("8", "View Product Reviews"));
        System.out.println(option("9", "View Sales Report"));

        System.out.println();
        System.out.println(subheader("🔔 Notifications"));
        System.out.println(option("10", "View Notifications"));
        System.out.println(option("11", "Mark Notification as Read"));

        System.out.println();
        System.out.println(subheader("👤 Account"));
        System.out.println(option("12", "Update Profile"));
        System.out.println(option("13", "Change Password"));
        System.out.println(option("14", "Logout"));

        System.out.print(inputPrompt("Enter your choice: "));

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.println(info("Adding new product..."));
                    sellerService.addProduct();
                    break;
                case 2:
                    System.out.println(info("Loading products..."));
                    sellerService.viewProducts();
                    break;
                case 3:
                    System.out.println(info("Updating product..."));
                    sellerService.updateProduct();
                    break;
                case 4:
                    System.out.println(info("Deleting product..."));
                    sellerService.deleteProduct();
                    break;
                case 5:
                    System.out.println(info("Loading orders..."));
                    sellerService.viewOrders();
                    break;
                case 6:
                    System.out.println(info("Updating order status..."));
                    sellerService.updateOrderStatus();
                    break;
                case 7:
                    System.out.println(warning("Checking low stock alerts..."));
                    sellerService.viewLowStockAlerts();
                    break;
                case 8:
                    System.out.println(info("Loading product reviews..."));
                    sellerService.viewProductReviews();
                    break;
                case 9:
                    System.out.println(info("Generating sales report..."));
                    sellerService.viewSalesReport();
                    break;
                case 10:
                    System.out.println(info("Loading notifications..."));
                    sellerService.viewNotifications();
                    break;
                case 11:
                    System.out.println(info("Marking notification as read..."));
                    sellerService.markNotificationAsRead();
                    break;
                case 12:
                    System.out.println(info("Updating profile..."));
                    sellerService.updateProfile();
                    break;
                case 13:
                    System.out.println(info("Changing password..."));
                    if (authService.changePassword()) {
                        System.out.println(success("Password changed successfully!"));
                    }
                    break;
                case 14:
                    authService.logout();
                    sellerService = null;
                    System.out.println(success("Logged out successfully!"));
                    break;
                default:
                    System.out.println(error("Invalid choice! Please try again."));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input! Please enter a number."));
        }
        System.out.println();
    }

    private void showFavoritesMenu() {
        while (true) {
            System.out.println(CYAN_BOLD_BRIGHT + "\n╔═══════════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN_BOLD_BRIGHT + "║" + RESET + PURPLE_BOLD_BRIGHT + "            FAVORITES MANAGEMENT               " + RESET + CYAN_BOLD_BRIGHT + "║" + RESET);
            System.out.println(CYAN_BOLD_BRIGHT + "╚═══════════════════════════════════════════════╝" + RESET);
            System.out.println();
            System.out.println(option("1", "View My Favorites"));
            System.out.println(option("2", "Add Product to Favorites"));
            System.out.println(option("3", "Remove Product from Favorites"));
            System.out.println(option("4", "Toggle Favorite (Quick Add/Remove)"));
            System.out.println(option("5", "Browse Products with Favorite Status"));
            System.out.println(option("6", "Back to Buyer Menu"));
            System.out.print(YELLOW_BOLD + "\nChoose an option: " + RESET);

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.println(info("Loading your favorites..."));
                        buyerService.viewFavorites();
                        break;
                    case 2:
                        System.out.println(info("Adding to favorites..."));
                        buyerService.addToFavorites();
                        break;
                    case 3:
                        System.out.println(info("Removing from favorites..."));
                        buyerService.removeFromFavorites();
                        break;
                    case 4:
                        System.out.println(info("Toggling favorite status..."));
                        buyerService.toggleFavorite();
                        break;
                    case 5:
                        System.out.println(info("Browsing products with favorite status..."));
                        buyerService.browseProductsWithFavorites();
                        break;
                    case 6:
                        System.out.println(info("Returning to buyer menu..."));
                        return;
                    default:
                        System.out.println(error("Invalid choice! Please try again."));
                }
            } catch (NumberFormatException e) {
                System.out.println(error("Please enter a valid number!"));
            }
            System.out.println();
        }
    }
}