package com.service.backend_service.service.impl;

import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Orders;
import com.service.backend_service.repo.OrdersRepository;
import com.service.backend_service.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrdersRepository orderRepo;

    public ResponseEntity<Map<String, String>> createCheckoutSession(Long orderId) {

        Orders order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order == null) {
            throw new RuntimeException("Order cannot be null");
        }
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:8080/payment/success?orderId=" + order.getId())
                            .setCancelUrl("http://localhost:8080/payment/cancel?orderId=" + order.getId())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(order.getTotalQuantity().longValue())
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("inr")
                                                            .setUnitAmount((long) (order.getTotalPrice() * 100))
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("Order Payment")
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

    @Override
    public ResponseEntity<String> paymentSuccess(Long orderId) {

        Orders order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        orderRepo.save(order);

        return ResponseEntity.ok("Payment Successful");
    }

    @Override
    public ResponseEntity<String> paymentCancel(Long orderId) {
        Orders order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepo.save(order);

        return ResponseEntity.ok("Payment Failure");
    }
}
