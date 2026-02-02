package com.revshop.test;

import com.revshop.dao.OrderDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void testBasicConnection() {
        OrderDAO orderDAO = new OrderDAO();
        assertNotNull(orderDAO, "OrderDAO should initialize");

        // Just test that we can get orders without exception
        var orders = orderDAO.getAllOrders();
        assertNotNull(orders, "Should get orders list");

        System.out.println("✅ OrderServiceTest passed - Found " + orders.size() + " orders");
    }

    @Test
    void testEnums() {
        // Test enum values exist
        assertNotNull(com.revshop.model.Order.OrderStatus.PENDING);
        assertNotNull(com.revshop.model.Order.PaymentStatus.PENDING);

        System.out.println("✅ Enums test passed");
    }
}