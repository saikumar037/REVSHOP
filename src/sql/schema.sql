-- RevShop Database Schema
-- This will clean everything and recreate fresh database

-- Drop entire database if exists (removes all tables)
DROP DATABASE IF EXISTS revshop;

-- Create fresh database
CREATE DATABASE revshop;

-- Use the database
USE revshop;

-- Drop tables in reverse order (due to foreign key constraints)
-- Note: We'll drop them in the schema creation phase

-- 1. Users table (base for both buyers and sellers)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    user_type ENUM('BUYER', 'SELLER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,  -- ADD THIS LINE
    is_active BOOLEAN DEFAULT TRUE,
    security_question VARCHAR(255),
    security_answer VARCHAR(255),
    password_hint VARCHAR(255)
);

-- 2. Sellers additional info
DROP TABLE IF EXISTS sellers;
CREATE TABLE sellers (
                         seller_id INT PRIMARY KEY,
                         business_name VARCHAR(100) NOT NULL,
                         business_address TEXT,
                         tax_id VARCHAR(50),
                         business_phone VARCHAR(15),
                         FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

-- 3. Products table
DROP TABLE IF EXISTS products;
CREATE TABLE products (
                          product_id INT PRIMARY KEY AUTO_INCREMENT,
                          seller_id INT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          category VARCHAR(50),
                          price DECIMAL(10, 2) NOT NULL,
                          mrp DECIMAL(10, 2) NOT NULL,
                          discount_price DECIMAL(10, 2),
                          stock_quantity INT DEFAULT 0,
                          threshold_quantity INT DEFAULT 5,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          is_active BOOLEAN DEFAULT TRUE,
                          FOREIGN KEY (seller_id) REFERENCES sellers(seller_id)
);

-- 4. Cart table
DROP TABLE IF EXISTS cart;
CREATE TABLE cart (
                      cart_id INT PRIMARY KEY AUTO_INCREMENT,
                      buyer_id INT NOT NULL,
                      product_id INT NOT NULL,
                      quantity INT NOT NULL DEFAULT 1,
                      added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (buyer_id) REFERENCES users(user_id),
                      FOREIGN KEY (product_id) REFERENCES products(product_id),
                      UNIQUE KEY unique_cart_item (buyer_id, product_id)
);

-- 5. Orders table
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
                        order_id INT PRIMARY KEY AUTO_INCREMENT,
                        buyer_id INT NOT NULL,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        total_amount DECIMAL(10, 2) NOT NULL,
                        shipping_address TEXT NOT NULL,
                        billing_address TEXT NOT NULL,
                        status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
                        payment_method VARCHAR(50),
                        payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
                        FOREIGN KEY (buyer_id) REFERENCES users(user_id)
);

-- 6. Order items table
DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
                             order_item_id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             price DECIMAL(10, 2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id),
                             FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 7. Reviews table
DROP TABLE IF EXISTS reviews;
CREATE TABLE reviews (
                         review_id INT PRIMARY KEY AUTO_INCREMENT,
                         product_id INT NOT NULL,
                         buyer_id INT NOT NULL,
                         order_id INT NOT NULL,
                         rating INT CHECK (rating >= 1 AND rating <= 5),
                         comment TEXT,
                         review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (product_id) REFERENCES products(product_id),
                         FOREIGN KEY (buyer_id) REFERENCES users(user_id),
                         FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- 8. Favorites table
DROP TABLE IF EXISTS favorites;
CREATE TABLE favorites (
                           favorite_id INT PRIMARY KEY AUTO_INCREMENT,
                           buyer_id INT NOT NULL,
                           product_id INT NOT NULL,
                           added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (buyer_id) REFERENCES users(user_id),
                           FOREIGN KEY (product_id) REFERENCES products(product_id),
                           UNIQUE KEY unique_favorite (buyer_id, product_id)
);

-- 9. Notifications table
DROP TABLE IF EXISTS notifications;
CREATE TABLE notifications (
                               notification_id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(50),
                               is_read BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 10. Insert sample data
INSERT INTO users (email, password, first_name, last_name, phone, address, user_type, security_question, security_answer) VALUES
                                                                                                                              ('buyer1@example.com', '$2a$10$ExampleHash1', 'John', 'Doe', '1234567890', '123 Main St, City', 'BUYER', 'What is your pet name?', 'Fluffy'),
                                                                                                                              ('seller1@example.com', '$2a$10$ExampleHash2', 'Jane', 'Smith', '0987654321', '456 Business Ave, City', 'SELLER', 'What is your birthplace?', 'New York'),
                                                                                                                              ('buyer2@example.com', '$2a$10$ExampleHash3', 'Bob', 'Johnson', '5551234567', '789 Park Rd, Town', 'BUYER', 'What is your favorite color?', 'Blue');

INSERT INTO sellers (seller_id, business_name, business_address, tax_id, business_phone) VALUES
    (2, 'Tech Gadgets Inc.', '456 Business Ave, City', 'TAX123456', '0987654321');

INSERT INTO products (seller_id, name, description, category, price, mrp, discount_price, stock_quantity, threshold_quantity) VALUES
                                                                                                                                  (2, 'Wireless Headphones', 'Noise cancelling wireless headphones', 'Electronics', 99.99, 129.99, 89.99, 50, 10),
                                                                                                                                  (2, 'Smart Watch', 'Fitness tracker with heart rate monitor', 'Electronics', 199.99, 249.99, NULL, 30, 5),
                                                                                                                                  (2, 'Laptop Backpack', 'Water resistant laptop backpack', 'Accessories', 49.99, 59.99, 39.99, 100, 20),
                                                                                                                                  (2, 'USB-C Cable', 'High speed charging cable', 'Electronics', 19.99, 24.99, NULL, 200, 50);

-- Display success message
SELECT 'Database revshop created successfully!' AS Message;
SELECT 'Total tables created: 9' AS Table_Count;
SHOW TABLES;