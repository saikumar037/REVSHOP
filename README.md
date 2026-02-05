# 🛒 RevShop – Console E-Commerce Application

## 📌 Overview

RevShop is a secure, console-based e-commerce application designed for both buyers and sellers.  
The application provides complete e-commerce functionality including user registration, product browsing, shopping cart management, order processing, and inventory management through a command-line interface.

This project follows a layered architecture and demonstrates backend development using Java, JDBC, and MySQL.

---

## ✨ Features

### 👤 Buyer Features
- User registration and authentication
- Browse products by category
- Search products by name
- Add/remove products from cart
- Checkout with shipping and billing information
- View order history
- Product reviews and ratings
- Favorite products
- Simulated payment processing

### 🏪 Seller Features
- Seller registration with business details
- Product inventory management
- View and manage orders
- Set product MRP and discount prices
- Inventory threshold alerts
- Product review monitoring

---

## 🛠 Technology Stack

- Java 11
- MySQL Database
- JDBC for database connectivity
- Log4j2 for logging
- JUnit for testing
- Maven for build management

---

## 🗄 Database Setup

1. Install MySQL
2. Create a database (example: `revshop_db`)
3. Run the SQL file to create tables:
   src/sql/schema.sql
4. Update database credentials in:
   src/main/resources/database.properties

## 🗄 Sample Configuration

#### db.url=jdbc:mysql://localhost:3306/revshop_db
#### db.username=root
#### db.password=your_password



---

## 📁 Project Structure

```text
RevShop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── revshop/
│   │   │           ├── MainApplication.java
│   │   │           ├── config/
│   │   │           │   ├── DatabaseConfig.java
│   │   │           │   └── LoggerConfig.java
│   │   │           ├── dao/
│   │   │           │   ├── BaseDAO.java
│   │   │           │   ├── UserDAO.java
│   │   │           │   ├── ProductDAO.java
│   │   │           │   ├── OrderDAO.java
│   │   │           │   ├── CartDAO.java
│   │   │           │   ├── ReviewDAO.java
│   │   │           │   └── SellerDAO.java
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   ├── Buyer.java
│   │   │           │   ├── Seller.java
│   │   │           │   ├── Product.java
│   │   │           │   ├── CartItem.java
│   │   │           │   ├── Order.java
│   │   │           │   ├── OrderItem.java
│   │   │           │   └── Review.java
│   │   │           ├── service/
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── BuyerService.java
│   │   │           │   ├── SellerService.java
│   │   │           │   ├── ProductService.java
│   │   │           │   ├── OrderService.java
│   │   │           │   ├── CartService.java
│   │   │           │   └── NotificationService.java
│   │   │           ├── util/
│   │   │           │   ├── DatabaseUtil.java
│   │   │           │   ├── ValidationUtil.java
│   │   │           │   ├── PasswordUtil.java
│   │   │           │   └── PaymentSimulator.java
│   │   │           └── menu/
│   │   │               ├── MainMenu.java
│   │   │               ├── BuyerMenu.java
│   │   │               └── SellerMenu.java
│   │   └── resources/
│   │       ├── log4j2.xml
│   │       └── database.properties
│   ├── test/
│   │   └── java/
│   │       └── com/
│   │           └── revshop/
│   │               └── test/
│   │                   ├── UserDAOTest.java
│   │                   ├── ProductServiceTest.java
│   │                   ├── OrderServiceTest.java
│   │                   └── AuthServiceTest.java
│   └── sql/
│       └── schema.sql
├── lib/
├── README.md
├── pom.xml
└── .gitignore


▶ Running the Application

1.Configure database connection in database.properties

2.Build the project:

    mvn clean compile
 
3.Run the application:

    mvn exec:java -Dexec.mainClass="com.revshop.MainApplication"



🧪 Testing

Run all unit tests using:

    mvn test


🚀 Future Enhancements

• Web-based interface

• Microservices architecture

• Email notifications

• Advanced payment gateway integration

• Recommendation engine