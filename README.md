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
├── src/main/java/com/revshop/
│ ├── config/ # Configuration classes
│ ├── dao/ # Data Access Objects
│ ├── model/ # Entity classes
│ ├── service/ # Business logic
│ ├── util/ # Utility classes
│ └── menu/ # Console menus
├── src/main/resources/ # Configuration files
├── src/test/java/ # Test classes
└── sql/ # Database schema

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