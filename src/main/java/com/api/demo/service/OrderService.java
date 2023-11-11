package com.api.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.api.demo.entity.Orders;
import com.api.demo.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Orders save(Orders order) {
        return orderRepository.save(order);
    }

    public List<Orders> findByAll(Long id) {
        return this.orderRepository.findByUserId(id);
    }

    public Orders findByOrderId(String id) {
        List<Orders> orders = this.orderRepository.findByOrderId(id);
        if (!orders.isEmpty()) {
            return orders.get(0);
        } else {
            return null;
        }
    
    }
}
