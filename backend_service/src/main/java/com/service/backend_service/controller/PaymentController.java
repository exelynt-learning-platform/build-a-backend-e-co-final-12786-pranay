package com.service.backend_service.controller;

import com.service.backend_service.service.PaymentService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final String callbackToken;

    public PaymentController(
            PaymentService paymentService,
            @Value("${payment.callback.token:}") String callbackToken) {
        this.paymentService = paymentService;
        this.callbackToken = callbackToken;
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<Map<String, String>> checkout(@PathVariable Long orderId) {
        return paymentService.createCheckoutSession(orderId);
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam Long orderId, @RequestParam String token) {
        validateCallbackToken(token);
        return paymentService.paymentSuccess(orderId);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel(@RequestParam Long orderId, @RequestParam String token) {
        validateCallbackToken(token);
        return paymentService.paymentCancel(orderId);
    }

    private void validateCallbackToken(String token) {
        if (!StringUtils.hasText(token) || !MessageDigest.isEqual(
                callbackToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid payment callback token"
            );
        }
    }
}
