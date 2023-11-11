package com.api.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.demo.entity.Orders;
import com.api.demo.entity.Product;
import com.api.demo.models.Costumer;
import com.api.demo.models.ErrorResponse;
import com.api.demo.models.ProductRequest;
import com.api.demo.service.OrderService;
import com.api.demo.service.PaymentService;
import com.api.demo.service.ProductService;
import com.api.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @PostMapping("create")
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody ProductRequest p) {

        Optional<Product> product = this.productService.findOneById(p.getProduct_id());
        if (!product.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Product does not exist"));
        }

        final var anUser = this.userService.getUser(request);

        Costumer customerResponse = this.paymentService.get(anUser);

        if (customerResponse == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Customer does not exist"));
        }

        Orders order = this.paymentService.create(customerResponse, product.get());
        if (order == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Payment failure"));
        }

        order.setProduct(product.get());
        order.setUser(anUser);

        System.out.println(order);

        Orders newOrder = orderService.save(order);

        return ResponseEntity.ok(newOrder);
    }

}
