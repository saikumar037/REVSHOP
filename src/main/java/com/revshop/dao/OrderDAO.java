package com.revshop.dao;

import com.revshop.model.Order;
import com.revshop.model.OrderItem;
import com.revshop.model.Order.OrderStatus;
import com.revshop.model.Order.PaymentStatus;
import com.revshop.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(OrderDAO.class);
    private ProductDAO productDAO;

    public OrderDAO() {
        this.productDAO = new ProductDAO();
    }

    // Create order
    public boolean createOrder(Order order) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "INSERT INTO orders (buyer_id, total_amount, shipping_address, " +
                "billing_address, status, payment_method, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, order.getBuyerId());
            pstmt.setBigDecimal(2, order.getTotalAmount());
            pstmt.setString(3, order.getShippingAddress());
            pstmt.setString(4, order.getBillingAddress());
            pstmt.setString(5, order.getStatus().toString());
            pstmt.setString(6, order.getPaymentMethod());
            pstmt.setString(7, order.getPaymentStatus().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    order.setOrderId(orderId);

                    // Create order items
                    if (createOrderItems(conn, orderId, order.getOrderItems())) {
                        conn.commit();
                        logger.info("Order created with ID: {}", orderId);
                        return true;
                    }
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            rollbackTransaction(conn);
            logger.error("Error creating order", e);
        } finally {
            setAutoCommit(conn, true);
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Create order items
    private boolean createOrderItems(Connection conn, int orderId, List<OrderItem> orderItems) throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

        try {
            pstmt = conn.prepareStatement(sql);
            for (OrderItem item : orderItems) {
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, item.getProductId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setBigDecimal(4, item.getPrice());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    logger.warn("Error closing PreparedStatement in createOrderItems", e);
                }
            }
        }
    }

    // Get order by ID
    public Order getOrderById(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItems(orderId));
                return order;
            }
        } catch (SQLException e) {
            logger.error("Error getting order by ID: {}", orderId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Get orders by buyer
    public List<Order> getOrdersByBuyer(int buyerId) {
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY order_date DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItems(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error getting orders for buyer: {}", buyerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return orders;
    }

    // Get orders by seller
    public List<Order> getOrdersBySeller(int sellerId) {
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT DISTINCT o.* FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE p.seller_id = ? ORDER BY o.order_date DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItems(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error getting orders for seller: {}", sellerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return orders;
    }

    // Get order items for a specific seller in an order
    public List<OrderItem> getOrderItemsForSeller(int orderId, int sellerId) {
        List<OrderItem> orderItems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT oi.*, p.* FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ? AND p.seller_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, sellerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));

                // Extract product
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setSellerId(rs.getInt("seller_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setMrp(rs.getBigDecimal("mrp"));
                product.setDiscountPrice(rs.getBigDecimal("discount_price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setThresholdQuantity(rs.getInt("threshold_quantity"));
                product.setCreatedAt(rs.getTimestamp("created_at"));
                product.setActive(rs.getBoolean("is_active"));

                item.setProduct(product);
                orderItems.add(item);
            }
        } catch (SQLException e) {
            logger.error("Error getting order items for seller {} in order {}", sellerId, orderId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return orderItems;
    }

    // Update order status for specific seller's items
    public boolean updateOrderStatusForSeller(int orderId, int sellerId, OrderStatus newStatus) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // First check if this seller has items in the order
        String checkSql = "SELECT COUNT(*) FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ? AND p.seller_id = ?";

        String updateSql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try {
            conn = getConnection();

            // Check if seller has items in this order
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, sellerId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                logger.warn("Seller {} has no items in order {}", sellerId, orderId);
                return false;
            }

            pstmt.close();

            // Update the order status
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setString(1, newStatus.toString());
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Order {} status updated to {} by seller {}", orderId, newStatus, sellerId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating order status for seller {} in order {}", sellerId, orderId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get order items
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT oi.*, p.* FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));

                // Extract product
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setSellerId(rs.getInt("seller_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setMrp(rs.getBigDecimal("mrp"));
                product.setDiscountPrice(rs.getBigDecimal("discount_price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setThresholdQuantity(rs.getInt("threshold_quantity"));
                product.setCreatedAt(rs.getTimestamp("created_at"));
                product.setActive(rs.getBoolean("is_active"));

                item.setProduct(product);
                orderItems.add(item);
            }
        } catch (SQLException e) {
            logger.error("Error getting order items for order: {}", orderId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return orderItems;
    }

    // Update order status
    public boolean updateOrderStatus(int orderId, OrderStatus status) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating order status: {}", orderId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Update payment status
    public boolean updatePaymentStatus(int orderId, PaymentStatus status) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating payment status: {}", orderId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM orders ORDER BY order_date DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error getting all orders", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return orders;
    }

    // Check if seller has items in order
    public boolean sellerHasItemsInOrder(int orderId, int sellerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT COUNT(*) FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ? AND p.seller_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, sellerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking if seller has items in order", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Get total sales amount for seller
    public BigDecimal getTotalSalesForSeller(int sellerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT COALESCE(SUM(oi.quantity * oi.price), 0) as total_sales " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE p.seller_id = ? AND o.payment_status = 'COMPLETED'";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total_sales");
            }
        } catch (SQLException e) {
            logger.error("Error getting total sales for seller: {}", sellerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return BigDecimal.ZERO;
    }

    // Helper method to extract order from ResultSet
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setBuyerId(rs.getInt("buyer_id"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setBillingAddress(rs.getString("billing_address"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(PaymentStatus.valueOf(rs.getString("payment_status")));
        return order;
    }
}