package com.api.demo.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.api.demo.entity.Orders;
import com.api.demo.service.OrderService;
import com.api.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> all(HttpServletRequest request) {
        final var anUser = this.userService.getUser(request);
        List<Orders> list = this.orderService.findByAll(anUser.getId());
        return ResponseEntity.ok(list);
    }

}
