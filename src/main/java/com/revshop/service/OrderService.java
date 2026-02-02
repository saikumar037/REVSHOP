package com.revshop.service;

import com.revshop.dao.OrderDAO;
import com.revshop.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OrderService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);
    private OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    public Order getOrderById(int orderId) {
        return orderDAO.getOrderById(orderId);
    }

    public List<Order> getOrdersByBuyer(int buyerId) {
        return orderDAO.getOrdersByBuyer(buyerId);
    }

    public List<Order> getOrdersBySeller(int sellerId) {
        return orderDAO.getOrdersBySeller(sellerId);
    }

    public boolean updateOrderStatus(int orderId, Order.OrderStatus status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }

    public boolean updatePaymentStatus(int orderId, Order.PaymentStatus status) {
        return orderDAO.updatePaymentStatus(orderId, status);
    }
}