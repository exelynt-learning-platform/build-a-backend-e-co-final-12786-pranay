package com.service.backend_service.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PaymentService {
    ResponseEntity<String> paymentCancel(String callbackId);

    ResponseEntity<String> paymentSuccess(String callbackId);

    ResponseEntity<Map<String, String>> createCheckoutSession(Long orderId);
}
