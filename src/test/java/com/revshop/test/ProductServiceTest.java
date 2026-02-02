package com.revshop.test;

import com.revshop.dao.ProductDAO;
import com.revshop.model.Product;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {
    private static ProductDAO productDAO;
    private static int testProductId;
    private static final int TEST_SELLER_ID = 2; // From sample data

    @BeforeAll
    static void setUp() {
        productDAO = new ProductDAO();
    }

    @Test
    @Order(1)
    void testGetProductsBySeller() {
        List<Product> products = productDAO.getProductsBySeller(TEST_SELLER_ID);
        assertNotNull(products, "Should get products list");
        assertFalse(products.isEmpty(), "Seller should have products");

        // Store first product ID for later tests
        if (!products.isEmpty()) {
            testProductId = products.get(0).getProductId();
            System.out.println("Using existing product ID: " + testProductId);
        }
    }

    @Test
    @Order(2)
    void testGetProductById() {
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Should find product by ID");
        assertEquals(testProductId, product.getProductId(), "Product ID should match");
        assertNotNull(product.getName(), "Product should have a name");
        assertNotNull(product.getCategory(), "Product should have a category");
    }

    @Test
    @Order(3)
    void testGetAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        assertNotNull(products, "Should get all products");
        assertFalse(products.isEmpty(), "Should have some products");

        // Check if our test product is in the list
        boolean found = products.stream()
                .anyMatch(product -> product.getProductId() == testProductId);
        assertTrue(found, "Test product should be in all products list");
    }

    @Test
    @Order(4)
    void testSearchProducts() {
        // Search for existing product
        List<Product> products = productDAO.searchProducts("Headphones");
        assertNotNull(products, "Search should return list");
        // May be empty if no match, that's OK

        // Search for non-existent product
        List<Product> noResults = productDAO.searchProducts("NonexistentProductXYZ");
        assertNotNull(noResults, "Should return empty list, not null");
    }

    @Test
    @Order(5)
    void testUpdateProduct() {
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Need product to update");

        // Store original values
        String originalName = product.getName();
        BigDecimal originalPrice = product.getPrice();

        // Update product
        product.setName("Updated Test Product");
        product.setPrice(new BigDecimal("109.99"));

        boolean result = productDAO.updateProduct(product);
        assertTrue(result, "Product update should succeed");

        // Verify update
        Product updatedProduct = productDAO.getProductById(testProductId);
        assertNotNull(updatedProduct, "Should retrieve updated product");
        assertEquals("Updated Test Product", updatedProduct.getName(), "Name should be updated");
        assertEquals(new BigDecimal("109.99"), updatedProduct.getPrice(), "Price should be updated");

        // Restore original values
        product.setName(originalName);
        product.setPrice(originalPrice);
        productDAO.updateProduct(product);
    }

    @Test
    @Order(6)
    void testUpdateStockQuantity() {
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Need product for stock test");

        int originalStock = product.getStockQuantity();

        // Decrease stock
        boolean result = productDAO.updateStockQuantity(testProductId, -5);
        assertTrue(result, "Stock update should succeed");

        // Verify decrease
        Product updatedProduct = productDAO.getProductById(testProductId);
        assertEquals(originalStock - 5, updatedProduct.getStockQuantity(),
                "Stock should decrease by 5");

        // Restore original stock
        productDAO.updateStockQuantity(testProductId, 5);
    }

    @Test
    @Order(7)
    void testGetAllCategories() {
        List<String> categories = productDAO.getAllCategories();
        assertNotNull(categories, "Should get categories list");
        assertFalse(categories.isEmpty(), "Should have some categories");

        // Sample data has these categories
        assertTrue(categories.contains("Electronics") ||
                        categories.contains("Accessories"),
                "Should contain expected categories");
    }

    @Test
    @Order(8)
    void testProductNotDeleted() {
        // This test verifies product is still active (not soft-deleted)
        Product product = productDAO.getProductById(testProductId);
        assertNotNull(product, "Product should still exist");
        assertTrue(product.isActive(), "Product should be active");
    }

    @Test
    @Order(9)
    void testLowStockProducts() {
        List<Product> lowStockProducts = productDAO.getLowStockProducts(TEST_SELLER_ID);
        assertNotNull(lowStockProducts, "Should get low stock products list");
        // May be empty if no low stock, that's OK
    }
}