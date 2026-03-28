package com.service.backend_service.controller;

import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Orders;
import com.service.backend_service.repo.OrdersRepository;
import com.service.backend_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<Map<String, String>> checkout(@PathVariable Long orderId) {


        return paymentService.createCheckoutSession(orderId);
    }


    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam Long orderId) {
        return paymentService.paymentSuccess(orderId);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel(@RequestParam Long orderId) {
        return paymentService.paymentCancel(orderId);
    }
}
