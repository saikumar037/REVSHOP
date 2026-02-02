package com.revshop.service;

import com.revshop.dao.ProductDAO;
import com.revshop.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ProductService {
    private static final Logger logger = LogManager.getLogger(ProductService.class);
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }

    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }

    public List<String> getAllCategories() {
        return productDAO.getAllCategories();
    }

    public boolean updateStock(int productId, int quantityChange) {
        return productDAO.updateStockQuantity(productId, quantityChange);
    }
}