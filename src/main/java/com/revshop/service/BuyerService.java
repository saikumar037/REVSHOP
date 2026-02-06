package com.revshop.service;

import com.revshop.dao.*;
import com.revshop.model.*;
import com.revshop.util.PaymentSimulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import static com.revshop.util.ConsoleColors.*;

public class BuyerService {
    private static final Logger logger = LogManager.getLogger(BuyerService.class);
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private ReviewDAO reviewDAO;
    private UserDAO userDAO;
    private NotificationService notificationService;
    private FavoriteDAO favoriteDAO;
    private Scanner scanner;
    private int currentBuyerId;

    public BuyerService(Scanner scanner, int buyerId) {
        this.productDAO = new ProductDAO();
        this.cartDAO = new CartDAO();
        this.orderDAO = new OrderDAO();
        this.reviewDAO = new ReviewDAO();
        this.userDAO = new UserDAO();
        this.notificationService = new NotificationService();
        this.favoriteDAO = new FavoriteDAO();
        this.scanner = scanner;
        this.currentBuyerId = buyerId;
    }

    // Browse all products
    public void browseProducts() {
        System.out.println(header("Browse Products"));
        List<Product> products = productDAO.getAllProducts();

        if (products.isEmpty()) {
            System.out.println(info("No products available."));
            return;
        }

        for (Product product : products) {
            System.out.println(productInfo(product.toString()));
            if (product.getDiscountPrice() != null) {
                System.out.printf(price("   (Original: $%.2f, Save: $%.2f)%n"),
                        product.getMrp().doubleValue(),
                        product.getMrp().subtract(product.getDiscountPrice()).doubleValue());
            }
            if (product.isLowStock()) {
                System.out.println(warning("   ⚠️  Low stock! Only " + product.getStockQuantity() + " left."));
            }
        }
    }

    // View product details
    public void viewProductDetails() {
        System.out.print(inputPrompt("Enter Product ID to view details: "));
        try {
            int productId = Integer.parseInt(scanner.nextLine());
            Product product = productDAO.getProductById(productId);

            if (product == null) {
                System.out.println(error("Product not found!"));
                return;
            }

            System.out.println(header("Product Details"));
            System.out.println(info("ID: " + product.getProductId()));
            System.out.println(info("Name: " + product.getName()));
            System.out.println(info("Description: " + product.getDescription()));
            System.out.println(info("Category: " + product.getCategory()));
            System.out.printf(price("Price: $%.2f%n"), product.getPrice().doubleValue());
            System.out.printf(price("MRP: $%.2f%n"), product.getMrp().doubleValue());
            if (product.getDiscountPrice() != null) {
                System.out.printf(price("Discounted Price: $%.2f%n"), product.getDiscountPrice().doubleValue());
                double discountPercent = (1 - product.getDiscountPrice().doubleValue() / product.getMrp().doubleValue()) * 100;
                System.out.printf(price("You Save: $%.2f (%.0f%%)%n"),
                        product.getMrp().subtract(product.getDiscountPrice()).doubleValue(),
                        discountPercent);
            }
            System.out.println(info("Stock: " + product.getStockQuantity()));

            // Check if it's in favorites
            boolean isFavorite = favoriteDAO.isFavorite(currentBuyerId, productId);
            System.out.println(info("❤️  In Favorites: " + (isFavorite ? "Yes" : "No")));

            if (product.isLowStock()) {
                System.out.println(warning("⚠️  Low stock alert! Only " + product.getStockQuantity() + " items left."));
            }

            // Show reviews
            List<Review> reviews = reviewDAO.getReviewsByProduct(productId);
            double avgRating = reviewDAO.getAverageRating(productId);

            System.out.println();
            System.out.printf(highlight("Average Rating: %.1f/5.0 ⭐%n"), avgRating);
            System.out.println(subheader("Reviews:"));
            if (reviews.isEmpty()) {
                System.out.println(info("   No reviews yet."));
            } else {
                for (Review review : reviews) {
                    System.out.println("   " + review);
                }
            }

        } catch (NumberFormatException e) {
            System.out.println(error("Invalid product ID!"));
        }
    }

    // Browse by category
    public void browseByCategory() {
        List<String> categories = productDAO.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println(info("No categories available."));
            return;
        }

        System.out.println(header("Categories"));
        for (int i = 0; i < categories.size(); i++) {
            System.out.println(option(String.valueOf(i + 1), categories.get(i)));
        }

        System.out.print(inputPrompt("Select category number: "));
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > categories.size()) {
                System.out.println(error("Invalid choice!"));
                return;
            }

            String category = categories.get(choice - 1);
            List<Product> products = productDAO.getProductsByCategory(category);

            System.out.println(header(category));
            if (products.isEmpty()) {
                System.out.println(info("No products in this category."));
            } else {
                for (Product product : products) {
                    boolean isFavorite = favoriteDAO.isFavorite(currentBuyerId, product.getProductId());
                    String favoriteStar = isFavorite ? "⭐ " : "";
                    System.out.println(favoriteStar + productInfo(product.toString()));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // Search products
    public void searchProducts() {
        System.out.print(inputPrompt("Enter search keyword: "));
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println(error("Please enter a search term!"));
            return;
        }

        List<Product> products = productDAO.searchProducts(keyword);

        System.out.println(search("Search Results for '" + keyword + "'"));
        if (products.isEmpty()) {
            System.out.println(info("No products found."));
        } else {
            System.out.println(success("Found " + products.size() + " product(s):"));
            for (Product product : products) {
                boolean isFavorite = favoriteDAO.isFavorite(currentBuyerId, product.getProductId());
                String favoriteStar = isFavorite ? "❤️  " : "";
                System.out.println(favoriteStar + productInfo(product.toString()));
            }
        }
    }

    // Add to cart
    public void addToCart() {
        System.out.print(inputPrompt("Enter Product ID to add to cart: "));
        try {
            int productId = Integer.parseInt(scanner.nextLine());

            Product product = productDAO.getProductById(productId);
            if (product == null) {
                System.out.println(error("Product not found!"));
                return;
            }

            if (product.getStockQuantity() <= 0) {
                System.out.println(error("Product out of stock!"));
                return;
            }

            System.out.print(inputPrompt("Quantity (default 1): "));
            String quantityStr = scanner.nextLine().trim();
            int quantity = quantityStr.isEmpty() ? 1 : Integer.parseInt(quantityStr);

            if (quantity <= 0) {
                System.out.println(error("Quantity must be positive!"));
                return;
            }

            if (quantity > product.getStockQuantity()) {
                System.out.println(error("Only " + product.getStockQuantity() + " items available!"));
                return;
            }

            CartItem cartItem = new CartItem(currentBuyerId, productId, quantity);
            if (cartDAO.addToCart(cartItem)) {
                System.out.println(success("Added to cart!"));
            } else {
                System.out.println(error("Failed to add to cart!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // View cart
    public void viewCart() {
        System.out.println(header("Shopping Cart 🛒"));
        List<CartItem> cartItems = cartDAO.getCartItems(currentBuyerId);

        if (cartItems.isEmpty()) {
            System.out.println(info("Your cart is empty."));
            return;
        }

        double total = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            boolean isFavorite = favoriteDAO.isFavorite(currentBuyerId, item.getProductId());
            String favoriteStar = isFavorite ? "❤️  " : "";
            System.out.println(option(String.valueOf(i + 1), favoriteStar + item.toString()));
            total += item.getTotalPrice();
        }

        System.out.println("------------------------");
        System.out.printf(price("Total: $%.2f%n"), total);
    }

    // Update cart item quantity
    public void updateCartQuantity() {
        System.out.print(inputPrompt("Enter cart item number to update: "));
        try {
            int itemNumber = Integer.parseInt(scanner.nextLine());
            List<CartItem> cartItems = cartDAO.getCartItems(currentBuyerId);

            if (itemNumber < 1 || itemNumber > cartItems.size()) {
                System.out.println(error("Invalid item number!"));
                return;
            }

            CartItem item = cartItems.get(itemNumber - 1);
            Product product = item.getProduct();

            System.out.print(inputPrompt("Enter new quantity (0 to remove): "));
            int newQuantity = Integer.parseInt(scanner.nextLine());

            if (newQuantity < 0) {
                System.out.println(error("Quantity cannot be negative!"));
                return;
            }

            if (newQuantity > product.getStockQuantity()) {
                System.out.println(error("Only " + product.getStockQuantity() + " items available!"));
                return;
            }

            if (cartDAO.updateCartItemQuantity(currentBuyerId, item.getProductId(), newQuantity)) {
                System.out.println(success("Cart updated!"));
            } else {
                System.out.println(error("Failed to update cart!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // Remove from cart
    public void removeFromCart() {
        System.out.print(inputPrompt("Enter Product ID to remove from cart: "));
        try {
            int productId = Integer.parseInt(scanner.nextLine());

            if (cartDAO.removeFromCart(currentBuyerId, productId)) {
                System.out.println(success("Removed from cart!"));
            } else {
                System.out.println(error("Item not found in cart!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid product ID!"));
        }
    }

    // Checkout
    public void checkout() {
        List<CartItem> cartItems = cartDAO.getCartItems(currentBuyerId);

        if (cartItems.isEmpty()) {
            System.out.println(error("Your cart is empty!"));
            return;
        }

        System.out.println(header("Checkout 💳"));

        // Display cart items
        double total = 0;
        for (CartItem item : cartItems) {
            System.out.println(productInfo(item.toString()));
            total += item.getTotalPrice();
        }

        System.out.println("------------------------");
        System.out.printf(price("Total: $%.2f%n"), total);

        // Get shipping address
        User buyer = userDAO.getUserById(currentBuyerId);
        System.out.println();
        System.out.println(info("Current Address: " + buyer.getAddress()));
        System.out.print(inputPrompt("Enter Shipping Address (press enter to use current): "));
        String shippingAddress = scanner.nextLine().trim();
        if (shippingAddress.isEmpty()) {
            shippingAddress = buyer.getAddress();
        }

        // Get billing address
        System.out.print(inputPrompt("Enter Billing Address (press enter to use shipping address): "));
        String billingAddress = scanner.nextLine().trim();
        if (billingAddress.isEmpty()) {
            billingAddress = shippingAddress;
        }

        // Select payment method
        String[] paymentMethods = PaymentSimulator.getAvailablePaymentMethods();
        System.out.println();
        System.out.println(subheader("Select Payment Method:"));
        for (int i = 0; i < paymentMethods.length; i++) {
            System.out.println(option(String.valueOf(i + 1), paymentMethods[i]));
        }

        System.out.print(inputPrompt("Enter choice: "));
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > paymentMethods.length) {
                System.out.println(error("Invalid choice!"));
                return;
            }

            String paymentMethod = paymentMethods[choice - 1];

            // Process payment
            System.out.println(info("Processing payment..."));
            if (!PaymentSimulator.processPayment(total, paymentMethod)) {
                System.out.println(error("Checkout cancelled due to payment failure!"));
                return;
            }

            // Create order
            Order order = new Order(currentBuyerId, BigDecimal.valueOf(total),
                    shippingAddress, billingAddress);
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus(Order.PaymentStatus.COMPLETED);

            // Convert cart items to order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem(0, cartItem.getProductId(),
                        cartItem.getQuantity(),
                        cartItem.getProduct().getFinalPrice());
                orderItems.add(orderItem);
            }
            order.setOrderItems(orderItems);

            // Save order
            System.out.println(info("Creating order..."));
            if (orderDAO.createOrder(order)) {
                // Clear cart
                cartDAO.clearCart(currentBuyerId);

                // Update stock
                for (CartItem cartItem : cartItems) {
                    productDAO.updateStockQuantity(cartItem.getProductId(), -cartItem.getQuantity());
                }

                // Send notification
                notificationService.sendNotification(currentBuyerId,
                        "Order #" + order.getOrderId() + " placed successfully!", "ORDER");

                System.out.println();
                System.out.println(success("Order placed successfully! 🎉"));
                System.out.println(info("Order ID: " + order.getOrderId()));
                System.out.println(info("Total: $" + total));
                System.out.println(info("Shipping to: " + shippingAddress));

                logger.info("Order placed: {} for buyer: {}", order.getOrderId(), currentBuyerId);
            } else {
                System.out.println(error("Failed to place order!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // View order history
    public void viewOrderHistory() {
        System.out.println(header("Order History 📋"));
        List<Order> orders = orderDAO.getOrdersByBuyer(currentBuyerId);

        if (orders.isEmpty()) {
            System.out.println(info("No orders found."));
            return;
        }

        for (Order order : orders) {
            System.out.println();
            System.out.println(orderStatus(order.toString()));
            System.out.println(info("Shipping: " + order.getShippingAddress()));
            System.out.println(info("Payment: " + order.getPaymentMethod() + " - " +
                    order.getPaymentStatus()));

            // Show order items
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                System.out.println(subheader("Items:"));
                for (OrderItem item : order.getOrderItems()) {
                    boolean isFavorite = favoriteDAO.isFavorite(currentBuyerId, item.getProductId());
                    String favoriteStar = isFavorite ? "❤️  " : "";
                    System.out.println("   " + favoriteStar + item);
                }
            }
        }
    }

    // Add review
    public void addReview() {
        System.out.println(header("Add Review ⭐"));

        // Get order ID
        System.out.print(inputPrompt("Enter Order ID: "));
        try {
            int orderId = Integer.parseInt(scanner.nextLine());
            Order order = orderDAO.getOrderById(orderId);

            if (order == null || order.getBuyerId() != currentBuyerId) {
                System.out.println(error("Order not found!"));
                return;
            }

            // Check if order is delivered
            if (order.getStatus() != Order.OrderStatus.DELIVERED) {
                System.out.println(error("You can only review delivered orders!"));
                return;
            }

            // Show order items
            System.out.println(subheader("Order Items:"));
            List<OrderItem> items = order.getOrderItems();
            for (int i = 0; i < items.size(); i++) {
                OrderItem item = items.get(i);
                boolean isFavorite = favoriteDAO.isFavorite(currentBuyerId, item.getProductId());
                String favoriteStar = isFavorite ? "❤️  " : "";
                System.out.println(option(String.valueOf(i + 1), favoriteStar + item.getProduct().getName()));
            }

            System.out.print(inputPrompt("Select item number to review: "));
            int itemChoice = Integer.parseInt(scanner.nextLine());

            if (itemChoice < 1 || itemChoice > items.size()) {
                System.out.println(error("Invalid choice!"));
                return;
            }

            OrderItem selectedItem = items.get(itemChoice - 1);
            int productId = selectedItem.getProductId();

            // Check if already reviewed
            if (reviewDAO.hasReviewed(currentBuyerId, productId, orderId)) {
                System.out.println(error("You have already reviewed this product!"));
                return;
            }

            // Get rating
            System.out.print(inputPrompt("Rating (1-5): "));
            int rating = Integer.parseInt(scanner.nextLine());

            if (rating < 1 || rating > 5) {
                System.out.println(error("Rating must be between 1 and 5!"));
                return;
            }

            System.out.print(inputPrompt("Comment: "));
            String comment = scanner.nextLine().trim();

            Review review = new Review(productId, currentBuyerId, orderId, rating, comment);
            if (reviewDAO.createReview(review)) {
                System.out.println(success("Review added successfully!"));
            } else {
                System.out.println(error("Failed to add review!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // View notifications
    public void viewNotifications() {
        System.out.println(header("Notifications 🔔"));
        notificationService.getNotifications(currentBuyerId);
    }

    // Mark notification as read
    public void markNotificationAsRead() {
        System.out.print(inputPrompt("Enter Notification ID to mark as read: "));
        try {
            int notificationId = Integer.parseInt(scanner.nextLine());
            if (notificationService.markAsRead(notificationId)) {
                System.out.println(success("Notification marked as read!"));
            } else {
                System.out.println(error("Notification not found!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // ================== FAVORITES METHODS ==================

    // View favorites
    public void viewFavorites() {
        System.out.println(header("My Favorites ❤️"));
        List<Product> favorites = favoriteDAO.getFavoriteProducts(currentBuyerId);

        int favoriteCount = favoriteDAO.getFavoriteCount(currentBuyerId);
        System.out.println(info("Total Favorites: " + favoriteCount));

        if (favorites.isEmpty()) {
            System.out.println(info("Your favorites list is empty."));
            return;
        }

        System.out.println(success("You have " + favorites.size() + " favorite product(s):"));
        System.out.println();

        for (int i = 0; i < favorites.size(); i++) {
            Product product = favorites.get(i);
            System.out.println(option(String.valueOf(i + 1), product.getName()));
            System.out.println("   " + info(product.getDescription()));
            System.out.printf("   " + price("Price: $%.2f"), product.getFinalPrice().doubleValue());
            if (product.getDiscountPrice() != null) {
                System.out.printf(price(" (Save: $%.2f)"),
                        product.getMrp().subtract(product.getDiscountPrice()).doubleValue());
            }
            System.out.println();
            System.out.println("   " + info("Stock: " + product.getStockQuantity()));
            System.out.println("   " + info("Category: " + product.getCategory()));

            if (product.isLowStock()) {
                System.out.println("   " + warning("⚠️  Low stock! Only " + product.getStockQuantity() + " left."));
            }

            System.out.println();
        }
    }

    // Add product to favorites
    public void addToFavorites() {
        System.out.println(header("Add to Favorites ❤️"));
        System.out.print(inputPrompt("Enter Product ID to add to favorites: "));

        try {
            int productId = Integer.parseInt(scanner.nextLine());
            Product product = productDAO.getProductById(productId);

            if (product == null) {
                System.out.println(error("Product not found!"));
                return;
            }

            if (!product.isActive()) {
                System.out.println(error("Product is not available!"));
                return;
            }

            if (favoriteDAO.isFavorite(currentBuyerId, productId)) {
                System.out.println(warning("Product is already in your favorites!"));
                return;
            }

            if (favoriteDAO.addToFavorites(currentBuyerId, productId)) {
                System.out.println(success("✓ Added to favorites!"));

                // Send notification
                notificationService.sendNotification(currentBuyerId,
                        "Added \"" + product.getName() + "\" to favorites!", "FAVORITE");
            } else {
                System.out.println(error("Failed to add to favorites!"));
            }

        } catch (NumberFormatException e) {
            System.out.println(error("Invalid Product ID!"));
        }
    }

    // Remove product from favorites
    public void removeFromFavorites() {
        System.out.println(header("Remove from Favorites"));

        // First show current favorites
        viewFavorites();

        System.out.print(inputPrompt("\nEnter Product ID to remove from favorites: "));

        try {
            int productId = Integer.parseInt(scanner.nextLine());

            if (favoriteDAO.removeFromFavorites(currentBuyerId, productId)) {
                System.out.println(success("✓ Removed from favorites!"));
            } else {
                System.out.println(error("Product not found in your favorites!"));
            }

        } catch (NumberFormatException e) {
            System.out.println(error("Invalid Product ID!"));
        }
    }

    // Toggle favorite status
    public void toggleFavorite() {
        System.out.println(header("Quick Favorite Toggle"));
        System.out.print(inputPrompt("Enter Product ID: "));

        try {
            int productId = Integer.parseInt(scanner.nextLine());
            Product product = productDAO.getProductById(productId);

            if (product == null) {
                System.out.println(error("Product not found!"));
                return;
            }

            if (favoriteDAO.isFavorite(currentBuyerId, productId)) {
                // Remove from favorites
                if (favoriteDAO.removeFromFavorites(currentBuyerId, productId)) {
                    System.out.println(success("✓ Removed from favorites!"));
                }
            } else {
                // Add to favorites
                if (favoriteDAO.addToFavorites(currentBuyerId, productId)) {
                    System.out.println(success("✓ Added to favorites!"));
                }
            }

        } catch (NumberFormatException e) {
            System.out.println(error("Invalid Product ID!"));
        }
    }

    // Browse products with favorite status
    public void browseProductsWithFavorites() {
        System.out.println(header("Browse Products with Favorite Status"));
        List<Product> products = productDAO.getAllProducts();

        if (products.isEmpty()) {
            System.out.println(info("No products available."));
            return;
        }

        List<Integer> favoriteIds = favoriteDAO.getFavoriteProductIds(currentBuyerId);

        for (Product product : products) {
            boolean isFavorite = favoriteIds.contains(product.getProductId());
            String favoriteStatus = isFavorite ? "❤️  " : "   ";

            System.out.println(favoriteStatus + productInfo(product.toString()));

            if (product.getDiscountPrice() != null) {
                System.out.printf(price("   (Original: $%.2f, Save: $%.2f)%n"),
                        product.getMrp().doubleValue(),
                        product.getMrp().subtract(product.getDiscountPrice()).doubleValue());
            }

            if (product.isLowStock()) {
                System.out.println(warning("   ⚠️  Low stock! Only " + product.getStockQuantity() + " left."));
            }

            System.out.println();
        }
    }
}