package com.revshop.service;

import com.revshop.dao.*;
import com.revshop.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static com.revshop.util.ConsoleColors.*;

public class SellerService {
    private static final Logger logger = LogManager.getLogger(SellerService.class);
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private ReviewDAO reviewDAO;
    private SellerDAO sellerDAO;
    private NotificationService notificationService;
    private Scanner scanner;
    private int currentSellerId;

    public SellerService(Scanner scanner, int sellerId) {
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.reviewDAO = new ReviewDAO();
        this.sellerDAO = new SellerDAO();
        this.notificationService = new NotificationService();
        this.scanner = scanner;
        this.currentSellerId = sellerId;
    }

    // Add product
    public void addProduct() {
        System.out.println(header("Add New Product"));

        System.out.print(inputPrompt("Product Name: "));
        String name = scanner.nextLine().trim();

        System.out.print(inputPrompt("Description: "));
        String description = scanner.nextLine().trim();

        System.out.print(inputPrompt("Category: "));
        String category = scanner.nextLine().trim();

        System.out.print(inputPrompt("Price: "));
        try {
            BigDecimal price = new BigDecimal(scanner.nextLine().trim());

            System.out.print(inputPrompt("MRP: "));
            BigDecimal mrp = new BigDecimal(scanner.nextLine().trim());

            System.out.print(inputPrompt("Discount Price (optional, press enter to skip): "));
            String discountStr = scanner.nextLine().trim();
            BigDecimal discountPrice = discountStr.isEmpty() ? null : new BigDecimal(discountStr);

            System.out.print(inputPrompt("Initial Stock Quantity: "));
            int stockQuantity = Integer.parseInt(scanner.nextLine().trim());

            System.out.print(inputPrompt("Stock Threshold (default 5): "));
            String thresholdStr = scanner.nextLine().trim();
            int thresholdQuantity = thresholdStr.isEmpty() ? 5 : Integer.parseInt(thresholdStr);

            Product product = new Product(currentSellerId, name, description, category, price, mrp, stockQuantity);
            product.setDiscountPrice(discountPrice);
            product.setThresholdQuantity(thresholdQuantity);

            if (productDAO.createProduct(product)) {
                System.out.println(success("Product added successfully!"));
                logger.info("Product added: {} by seller: {}", name, currentSellerId);
            } else {
                System.out.println(error("Failed to add product!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid number format!"));
        } catch (Exception e) {
            System.out.println(error("Error: " + e.getMessage()));
        }
    }

    // View products
    public void viewProducts() {
        System.out.println(header("My Products 📦"));
        List<Product> products = productDAO.getProductsBySeller(currentSellerId);

        if (products.isEmpty()) {
            System.out.println(info("No products found."));
            return;
        }

        for (Product product : products) {
            System.out.println(productInfo(product.toString()));
            if (product.isLowStock()) {
                System.out.println(warning("   ⚠️  LOW STOCK! Only " + product.getStockQuantity() + " left."));
            }
        }
    }

    // Update product
    public void updateProduct() {
        System.out.print(inputPrompt("Enter Product ID to update: "));
        try {
            int productId = Integer.parseInt(scanner.nextLine());
            Product product = productDAO.getProductById(productId);

            if (product == null || product.getSellerId() != currentSellerId) {
                System.out.println(error("Product not found or you don't have permission!"));
                return;
            }

            System.out.println(header("Update Product"));
            System.out.println(info("Leave blank to keep current value."));

            System.out.print(inputPrompt("Name (" + product.getName() + "): "));
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) product.setName(name);

            System.out.print(inputPrompt("Description (" + product.getDescription() + "): "));
            String description = scanner.nextLine().trim();
            if (!description.isEmpty()) product.setDescription(description);

            System.out.print(inputPrompt("Category (" + product.getCategory() + "): "));
            String category = scanner.nextLine().trim();
            if (!category.isEmpty()) product.setCategory(category);

            System.out.print(inputPrompt("Price ($" + product.getPrice() + "): "));
            String priceStr = scanner.nextLine().trim();
            if (!priceStr.isEmpty()) product.setPrice(new BigDecimal(priceStr));

            System.out.print(inputPrompt("MRP ($" + product.getMrp() + "): "));
            String mrpStr = scanner.nextLine().trim();
            if (!mrpStr.isEmpty()) product.setMrp(new BigDecimal(mrpStr));

            System.out.print(inputPrompt("Discount Price ($" +
                    (product.getDiscountPrice() != null ? product.getDiscountPrice() : "none") +
                    "): "));
            String discountStr = scanner.nextLine().trim();
            if (discountStr.equalsIgnoreCase("none") || discountStr.isEmpty()) {
                product.setDiscountPrice(null);
            } else if (!discountStr.isEmpty()) {
                product.setDiscountPrice(new BigDecimal(discountStr));
            }

            System.out.print(inputPrompt("Stock Quantity (" + product.getStockQuantity() + "): "));
            String stockStr = scanner.nextLine().trim();
            if (!stockStr.isEmpty()) product.setStockQuantity(Integer.parseInt(stockStr));

            System.out.print(inputPrompt("Threshold Quantity (" + product.getThresholdQuantity() + "): "));
            String thresholdStr = scanner.nextLine().trim();
            if (!thresholdStr.isEmpty()) product.setThresholdQuantity(Integer.parseInt(thresholdStr));

            if (productDAO.updateProduct(product)) {
                System.out.println(success("Product updated successfully!"));
            } else {
                System.out.println(error("Failed to update product!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid number format!"));
        }
    }

    // Delete product
    public void deleteProduct() {
        System.out.print(inputPrompt("Enter Product ID to delete: "));
        try {
            int productId = Integer.parseInt(scanner.nextLine());
            Product product = productDAO.getProductById(productId);

            if (product == null || product.getSellerId() != currentSellerId) {
                System.out.println(error("Product not found or you don't have permission!"));
                return;
            }

            System.out.print(inputPrompt("Are you sure you want to delete '" + product.getName() + "'? (yes/no): "));
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("yes")) {
                if (productDAO.deleteProduct(productId)) {
                    System.out.println(success("Product deleted successfully!"));
                } else {
                    System.out.println(error("Failed to delete product!"));
                }
            } else {
                System.out.println(info("Deletion cancelled."));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid product ID!"));
        }
    }

    // View orders
    public void viewOrders() {
        System.out.println(header("Orders 📋"));
        List<Order> orders = orderDAO.getOrdersBySeller(currentSellerId);

        if (orders.isEmpty()) {
            System.out.println(info("No orders found."));
            return;
        }

        for (Order order : orders) {
            System.out.println();
            System.out.println(orderStatus(order.toString()));
            System.out.println(info("Buyer ID: " + order.getBuyerId()));
            System.out.println(info("Shipping: " + order.getShippingAddress()));

            // Show items from this seller
            System.out.println(subheader("Items from you:"));
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProduct().getSellerId() == currentSellerId) {
                    System.out.println("   " + item);
                }
            }
        }
    }

    // Update order status
    public void updateOrderStatus() {
        System.out.print(inputPrompt("Enter Order ID to update status: "));
        try {
            int orderId = Integer.parseInt(scanner.nextLine());
            Order order = orderDAO.getOrderById(orderId);

            if (order == null) {
                System.out.println(error("Order not found!"));
                return;
            }

            // Check if seller has items in this order
            boolean hasItems = false;
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProduct().getSellerId() == currentSellerId) {
                    hasItems = true;
                    break;
                }
            }

            if (!hasItems) {
                System.out.println(error("You don't have any items in this order!"));
                return;
            }

            System.out.println();
            System.out.println(info("Current Status: " + order.getStatus()));
            System.out.println(subheader("Select new status:"));
            System.out.println(option("1", "PROCESSING"));
            System.out.println(option("2", "SHIPPED"));
            System.out.println(option("3", "DELIVERED"));
            System.out.println(option("4", "CANCELLED"));

            System.out.print(inputPrompt("Enter choice: "));
            int choice = Integer.parseInt(scanner.nextLine());

            Order.OrderStatus newStatus;
            switch (choice) {
                case 1:
                    newStatus = Order.OrderStatus.PROCESSING;
                    break;
                case 2:
                    newStatus = Order.OrderStatus.SHIPPED;
                    break;
                case 3:
                    newStatus = Order.OrderStatus.DELIVERED;
                    break;
                case 4:
                    newStatus = Order.OrderStatus.CANCELLED;

                    // Restore stock if cancelled
                    System.out.print(inputPrompt("Restore stock quantities? (yes/no): "));
                    String restoreStock = scanner.nextLine().trim();
                    if (restoreStock.equalsIgnoreCase("yes")) {
                        for (OrderItem item : order.getOrderItems()) {
                            if (item.getProduct().getSellerId() == currentSellerId) {
                                productDAO.updateStockQuantity(item.getProductId(), item.getQuantity());
                            }
                        }
                        System.out.println(success("Stock quantities restored!"));
                    }
                    break;
                default:
                    System.out.println(error("Invalid choice!"));
                    return;
            }

            if (orderDAO.updateOrderStatus(orderId, newStatus)) {
                System.out.println(success("Order status updated to " + newStatus + "!"));

                // Send notification to buyer
                notificationService.sendNotification(order.getBuyerId(),
                        "Order #" + orderId + " status updated to " + newStatus, "ORDER_UPDATE");
            } else {
                System.out.println(error("Failed to update order status!"));
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid input!"));
        }
    }

    // View low stock alerts
    public void viewLowStockAlerts() {
        System.out.println(header("Low Stock Alerts 🚨"));
        List<Product> lowStockProducts = productDAO.getLowStockProducts(currentSellerId);

        if (lowStockProducts.isEmpty()) {
            System.out.println(info("No low stock alerts. 🎉"));
            return;
        }

        System.out.println(warning("You have " + lowStockProducts.size() + " product(s) with low stock:"));
        for (Product product : lowStockProducts) {
            System.out.println(productInfo(product.toString()));
            System.out.println(alert("   ALERT: Only " + product.getStockQuantity() +
                    " left (threshold: " + product.getThresholdQuantity() + ")"));
        }
    }

    // View product reviews
    public void viewProductReviews() {
        System.out.print(inputPrompt("Enter Product ID to view reviews: "));
        try {
            int productId = Integer.parseInt(scanner.nextLine());
            Product product = productDAO.getProductById(productId);

            if (product == null || product.getSellerId() != currentSellerId) {
                System.out.println(error("Product not found or you don't have permission!"));
                return;
            }

            List<Review> reviews = reviewDAO.getReviewsByProduct(productId);
            double avgRating = reviewDAO.getAverageRating(productId);

            System.out.println();
            System.out.println(header("Reviews for " + product.getName()));
            System.out.printf(highlight("Average Rating: %.1f/5.0 ⭐%n"), avgRating);

            if (reviews.isEmpty()) {
                System.out.println(info("No reviews yet."));
            } else {
                for (Review review : reviews) {
                    System.out.println();
                    System.out.println("   " + review);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(error("Invalid product ID!"));
        }
    }

    // Update seller profile
    public void updateProfile() {
        Seller seller = sellerDAO.getSellerById(currentSellerId);

        if (seller == null) {
            System.out.println(error("Seller not found!"));
            return;
        }

        System.out.println(header("Update Seller Profile"));
        System.out.println(info("Leave blank to keep current value."));

        System.out.print(inputPrompt("Business Name (" + seller.getBusinessName() + "): "));
        String businessName = scanner.nextLine().trim();
        if (!businessName.isEmpty()) seller.setBusinessName(businessName);

        System.out.print(inputPrompt("Business Address (" + seller.getBusinessAddress() + "): "));
        String businessAddress = scanner.nextLine().trim();
        if (!businessAddress.isEmpty()) seller.setBusinessAddress(businessAddress);

        System.out.print(inputPrompt("Tax ID (" + seller.getTaxId() + "): "));
        String taxId = scanner.nextLine().trim();
        if (!taxId.isEmpty()) seller.setTaxId(taxId);

        System.out.print(inputPrompt("Business Phone (" + seller.getBusinessPhone() + "): "));
        String businessPhone = scanner.nextLine().trim();
        if (!businessPhone.isEmpty()) seller.setBusinessPhone(businessPhone);

        if (sellerDAO.updateSeller(seller)) {
            System.out.println(success("Profile updated successfully!"));
        } else {
            System.out.println(error("Failed to update profile!"));
        }
    }

    // View sales report
    public void viewSalesReport() {
        System.out.println(header("Sales Report 📊"));
        List<Order> orders = orderDAO.getOrdersBySeller(currentSellerId);

        if (orders.isEmpty()) {
            System.out.println(info("No sales yet."));
            return;
        }

        double totalSales = 0;
        int totalItems = 0;
        int completedOrders = 0;

        for (Order order : orders) {
            if (order.getStatus() == Order.OrderStatus.DELIVERED &&
                    order.getPaymentStatus() == Order.PaymentStatus.COMPLETED) {
                completedOrders++;

                for (OrderItem item : order.getOrderItems()) {
                    if (item.getProduct().getSellerId() == currentSellerId) {
                        totalSales += item.getTotalPrice().doubleValue();
                        totalItems += item.getQuantity();
                    }
                }
            }
        }

        System.out.println(info("Completed Orders: " + completedOrders));
        System.out.println(info("Total Items Sold: " + totalItems));
        System.out.printf(price("Total Sales: $%.2f%n"), totalSales);

        // Show inventory status
        System.out.println();
        System.out.println(header("Inventory Status 📦"));
        List<Product> products = productDAO.getProductsBySeller(currentSellerId);
        for (Product product : products) {
            String stockStatus = product.isLowStock() ? "LOW STOCK" : "IN STOCK";
            String statusColor = product.isLowStock() ? RED_BRIGHT : GREEN_BRIGHT;
            System.out.printf("%-20s: %3d units (%s)%n",
                    product.getName(), product.getStockQuantity(),
                    statusColor + stockStatus + RESET);
        }
    }

    // View notifications
    public void viewNotifications() {
        System.out.println(header("Notifications 🔔"));
        notificationService.getNotifications(currentSellerId);
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
}