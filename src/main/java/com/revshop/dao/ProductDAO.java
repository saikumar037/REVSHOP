package com.revshop.dao;

import com.revshop.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(ProductDAO.class);

    // Create product
    public boolean createProduct(Product product) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "INSERT INTO products (seller_id, name, description, category, price, mrp, " +
                "discount_price, stock_quantity, threshold_quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, product.getSellerId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getCategory());
            pstmt.setBigDecimal(5, product.getPrice());
            pstmt.setBigDecimal(6, product.getMrp());
            pstmt.setBigDecimal(7, product.getDiscountPrice());
            pstmt.setInt(8, product.getStockQuantity());
            pstmt.setInt(9, product.getThresholdQuantity());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    product.setProductId(rs.getInt(1));
                    logger.info("Product created with ID: {}", product.getProductId());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating product: {}", product.getName(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Get product by ID
    public Product getProductById(int productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM products WHERE product_id = ? AND is_active = TRUE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting product by ID: {}", productId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Get all products
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM products WHERE is_active = TRUE ORDER BY name";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all products", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return products;
    }

    // Get products by seller
    public List<Product> getProductsBySeller(int sellerId) {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM products WHERE seller_id = ? AND is_active = TRUE ORDER BY name";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting products for seller: {}", sellerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return products;
    }

    // Get products by category
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM products WHERE category = ? AND is_active = TRUE ORDER BY name";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting products by category: {}", category, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return products;
    }

    // Search products
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM products WHERE (name LIKE ? OR description LIKE ? OR category LIKE ?) " +
                "AND is_active = TRUE ORDER BY name";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching products with keyword: {}", keyword, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return products;
    }

    // Update product
    public boolean updateProduct(Product product) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE products SET name = ?, description = ?, category = ?, price = ?, " +
                "mrp = ?, discount_price = ?, stock_quantity = ?, threshold_quantity = ? " +
                "WHERE product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setString(3, product.getCategory());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setBigDecimal(5, product.getMrp());
            pstmt.setBigDecimal(6, product.getDiscountPrice());
            pstmt.setInt(7, product.getStockQuantity());
            pstmt.setInt(8, product.getThresholdQuantity());
            pstmt.setInt(9, product.getProductId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating product: {}", product.getProductId(), e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Update stock quantity
    public boolean updateStockQuantity(int productId, int quantityChange) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating stock for product: {}", productId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Delete product (soft delete)
    public boolean deleteProduct(int productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE products SET is_active = FALSE WHERE product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting product: {}", productId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get low stock products for seller
    public List<Product> getLowStockProducts(int sellerId) {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM products WHERE seller_id = ? AND stock_quantity <= threshold_quantity " +
                "AND is_active = TRUE ORDER BY stock_quantity";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting low stock products for seller: {}", sellerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return products;
    }

    // Get all categories
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT DISTINCT category FROM products WHERE is_active = TRUE ORDER BY category";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            logger.error("Error getting all categories", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return categories;
    }

    // Helper method to extract product from ResultSet
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
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
        return product;
    }
}