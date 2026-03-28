package com.service.backend_service.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PaymentService {
    ResponseEntity<String> paymentCancel(Long orderId);

    ResponseEntity<String> paymentSuccess(Long orderId);

    ResponseEntity<Map<String, String>> createCheckoutSession(Long orderId);
}
