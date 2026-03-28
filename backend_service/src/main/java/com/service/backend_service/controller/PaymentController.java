package com.service.backend_service.controller;

import com.service.backend_service.service.PaymentService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<Map<String, String>> checkout(@PathVariable Long orderId) {
        return paymentService.createCheckoutSession(orderId);
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam String callbackId) {
        return paymentService.paymentSuccess(callbackId);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel(@RequestParam String callbackId) {
        return paymentService.paymentCancel(callbackId);
    }
}
