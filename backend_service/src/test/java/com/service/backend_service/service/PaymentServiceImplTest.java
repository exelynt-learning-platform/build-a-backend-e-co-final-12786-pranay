package com.service.backend_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Order;
import com.service.backend_service.repo.OrdersRepository;
import com.service.backend_service.service.impl.PaymentServiceImpl;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private OrdersRepository orderRepo;

    private PaymentServiceImpl paymentService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                orderRepo,
                "http://localhost:8080/payment/success",
                "http://localhost:8080/payment/cancel",
                "usd",
                "callback-token"
        );
    }

    @Test
    void paymentSuccessUpdatesOrderState() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<String> response = paymentService.paymentSuccess(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
        assertEquals(PaymentStatus.COMPLETED, order.getPaymentStatus());
        verify(orderRepo).save(order);
    }

    @Test
    void paymentCancelUpdatesOrderState() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<String> response = paymentService.paymentCancel(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(PaymentStatus.FAILED, order.getPaymentStatus());
        verify(orderRepo).save(order);
    }

    @Test
    void createCheckoutSessionReturnsCheckoutUrl() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setTotalQuantity(2);
        order.setTotalPrice(100.0);

        Session session = new Session();
        session.setUrl("http://checkout");

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            ArgumentCaptor<SessionCreateParams> paramsCaptor = ArgumentCaptor.forClass(SessionCreateParams.class);
            mocked.when(() -> Session.create(org.mockito.ArgumentMatchers.any(SessionCreateParams.class)))
                    .thenReturn(session);

            ResponseEntity<Map<String, String>> response = paymentService.createCheckoutSession(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().containsKey("checkoutUrl"));
            assertEquals("http://checkout", response.getBody().get("checkoutUrl"));
            mocked.verify(() -> Session.create(paramsCaptor.capture()));
            SessionCreateParams params = paramsCaptor.getValue();
            assertEquals("usd", params.getLineItems().get(0).getPriceData().getCurrency());
            assertEquals("http://localhost:8080/payment/success?orderId=1&token=callback-token", params.getSuccessUrl());
            assertEquals("http://localhost:8080/payment/cancel?orderId=1&token=callback-token", params.getCancelUrl());
        }
    }
}
