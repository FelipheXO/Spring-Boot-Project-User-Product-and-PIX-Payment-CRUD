package com.api.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.demo.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o FROM Orders o WHERE o.user.id = :userId")
    List<Orders> findByUserId(Long userId);

    @Query("SELECT o FROM Orders o WHERE o.id_order = :orderId")
    List<Orders> findByOrderId(String orderId);
}
