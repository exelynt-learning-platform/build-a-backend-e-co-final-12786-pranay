package com.service.backend_service.service.impl;

import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Order;
import com.service.backend_service.repo.OrderRepository;
import com.service.backend_service.service.PaymentService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String DEFAULT_PAYMENT_PRODUCT_NAME = "Order Payment";

    private final OrderRepository orderRepo;
    private final String successUrl;
    private final String cancelUrl;
    private final String currency;
    private final String productName;
    private final long callbackTtlMinutes;
    private final Map<String, CallbackContext> callbackContexts = new ConcurrentHashMap<>();

    public PaymentServiceImpl(
            OrderRepository orderRepo,
            @Value("${payment.success.url:http://localhost:8080/payment/success}") String successUrl,
            @Value("${payment.cancel.url:http://localhost:8080/payment/cancel}") String cancelUrl,
            @Value("${payment.currency:inr}") String currency,
            @Value("${payment.product-name:" + DEFAULT_PAYMENT_PRODUCT_NAME + "}") String productName,
            @Value("${payment.callback.ttl-minutes:15}") long callbackTtlMinutes) {
        this.orderRepo = orderRepo;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.currency = currency;
        this.productName = productName;
        this.callbackTtlMinutes = callbackTtlMinutes;
    }

    public ResponseEntity<Map<String, String>> createCheckoutSession(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        try {
            String successCallbackId = registerCallback(order.getId());
            String cancelCallbackId = registerCallback(order.getId());
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(buildRedirectUrl(successUrl, successCallbackId))
                            .setCancelUrl(buildRedirectUrl(cancelUrl, cancelCallbackId))
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(order.getTotalQuantity().longValue())
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(currency)
                                                            .setUnitAmount((long) (order.getTotalPrice() * 100))
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName(productName)
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            Session session = Session.create(params);

            Map<String, String> response = new HashMap<>();
            response.put("checkoutUrl", session.getUrl());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // You can log it if needed
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    private String buildRedirectUrl(String baseUrl, String callbackId) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("callbackId", callbackId)
                .build()
                .toUriString();
    }

    @Override
    public ResponseEntity<String> paymentSuccess(String callbackId) {
        Order order = resolveOrderForCallback(callbackId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired payment callback");
        }
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        orderRepo.save(order);

        return ResponseEntity.ok("Payment Successful");
    }

    @Override
    public ResponseEntity<String> paymentCancel(String callbackId) {
        Order order = resolveOrderForCallback(callbackId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired payment callback");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepo.save(order);

        return ResponseEntity.ok("Payment Failure");
    }

    private String registerCallback(Long orderId) {
        cleanupExpiredCallbacks();
        String callbackId = UUID.randomUUID().toString();
        callbackContexts.put(callbackId, new CallbackContext(orderId, Instant.now().plus(callbackTtlMinutes, ChronoUnit.MINUTES)));
        return callbackId;
    }

    private Order resolveOrderForCallback(String callbackId) {
        CallbackContext callbackContext = callbackContexts.remove(callbackId);
        if (callbackContext == null || callbackContext.expiresAt().isBefore(Instant.now())) {
            return null;
        }
        return orderRepo.findById(callbackContext.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Scheduled(fixedDelayString = "${payment.callback.cleanup-interval-ms:300000}")
    void cleanupExpiredCallbacks() {
        Instant now = Instant.now();
        callbackContexts.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private record CallbackContext(Long orderId, Instant expiresAt) {
    }
}
