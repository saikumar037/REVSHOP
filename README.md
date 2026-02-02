# RevShop - Console E-Commerce Application

## Overview
RevShop is a secure console-based e-commerce application for both buyers and sellers. The application provides comprehensive e-commerce functionality including user registration, product browsing, shopping cart management, order processing, and inventory management.

## Features

### Buyer Features
- User registration and authentication
- Browse products by category or search
- Add/remove products from cart
- Checkout with shipping/billing information
- Order history view
- Product reviews and ratings
- Favorite products
- Simulated payment processing

### Seller Features
- Seller registration with business details
- Product inventory management
- View and manage orders
- Set discount prices and MRP
- Inventory threshold alerts
- Product review monitoring

## Technology Stack
- Java 11
- MySQL Database
- JDBC for database connectivity
- Log4j2 for logging
- JUnit for testing
- Maven for build management

## Database Setup
1. Install MySQL
2. Run the schema.sql file to create database and tables
3. Update database.properties with your credentials

## Project Structure

RevShop/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   └── revshop/
│   │   │   │       ├── MainApplication.java
│   │   │   │       ├── config/
│   │   │   │       │   ├── DatabaseConfig.java
│   │   │   │       │   └── LoggerConfig.java
│   │   │   │       ├── dao/
│   │   │   │       │   ├── BaseDAO.java
│   │   │   │       │   ├── UserDAO.java
│   │   │   │       │   ├── ProductDAO.java
│   │   │   │       │   ├── OrderDAO.java
│   │   │   │       │   ├── CartDAO.java
│   │   │   │       │   ├── ReviewDAO.java
│   │   │   │       │   └── SellerDAO.java
│   │   │   │       ├── model/
│   │   │   │       │   ├── User.java
│   │   │   │       │   ├── Buyer.java
│   │   │   │       │   ├── Seller.java
│   │   │   │       │   ├── Product.java
│   │   │   │       │   ├── CartItem.java
│   │   │   │       │   ├── Order.java
│   │   │   │       │   ├── OrderItem.java
│   │   │   │       │   └── Review.java
│   │   │   │       ├── service/
│   │   │   │       │   ├── AuthService.java
│   │   │   │       │   ├── BuyerService.java
│   │   │   │       │   ├── SellerService.java
│   │   │   │       │   ├── ProductService.java
│   │   │   │       │   ├── OrderService.java
│   │   │   │       │   ├── CartService.java
│   │   │   │       │   └── NotificationService.java
│   │   │   │       ├── util/
│   │   │   │       │   ├── DatabaseUtil.java
│   │   │   │       │   ├── ValidationUtil.java
│   │   │   │       │   ├── PasswordUtil.java
│   │   │   │       │   └── PaymentSimulator.java
│   │   │   │       └── menu/
│   │   │   │           ├── MainMenu.java
│   │   │   │           ├── BuyerMenu.java
│   │   │   │           └── SellerMenu.java
│   │   │   └── resources/
│   │   │       ├── log4j2.xml
│   │   │       └── database.properties
│   │   └── test/
│   │       └── java/
│   │           └── com/
│   │               └── revshop/
│   │                   └── test/
│   │                       ├── UserDAOTest.java
│   │                       ├── ProductServiceTest.java
│   │                       ├── OrderServiceTest.java
│   │                       └── AuthServiceTest.java
│   └── sql/
│       └── schema.sql
├── lib/
├── README.md
├── pom.xml
└── .gitignore

## Running the Application
1. Configure database connection in `database.properties`
2. Build project: `mvn clean compile`
3. Run: `mvn exec:java -Dexec.mainClass="com.revshop.MainApplication"`

## Testing
Run tests with: `mvn test`

## Future Enhancements
- Web interface
- Microservices architecture
- Email notifications
- Advanced payment integration
- Recommendation engine