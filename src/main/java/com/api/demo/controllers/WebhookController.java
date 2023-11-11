package com.api.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.demo.entity.Orders;
import com.api.demo.models.ErrorResponse;
import com.api.demo.service.OrderService;

import kong.unirest.json.JSONObject;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    OrderService orderService;

    @PostMapping("pagarme")
    public ResponseEntity<?> login(@RequestBody String result) {
        JSONObject webhook = new JSONObject(result);

        JSONObject data = webhook.getJSONObject("data");
        String id = data.getString("id");
        String status = data.getString("status");

        Orders order = this.orderService.findByOrderId(id);
        order.setStatus(status);
        this.orderService.save(order);

        return ResponseEntity.ok(new ErrorResponse("Status updated"));
    }

}