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
                "Stripe Order Payment",
                15
        );
    }

    @Test
    void paymentSuccessUpdatesOrderState() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalQuantity(1);
        order.setTotalPrice(10.0);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        String callbackId = registerCallbackForOrder(order, true);

        ResponseEntity<String> response = paymentService.paymentSuccess(callbackId);

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

        String callbackId = registerCallbackForOrder(order, false);
        ResponseEntity<String> response = paymentService.paymentCancel(callbackId);

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
            assertEquals("Stripe Order Payment", params.getLineItems().get(0).getPriceData().getProductData().getName());
            assertTrue(params.getSuccessUrl().startsWith("http://localhost:8080/payment/success?callbackId="));
            assertTrue(params.getCancelUrl().startsWith("http://localhost:8080/payment/cancel?callbackId="));
            assertTrue(extractCallbackId(params.getSuccessUrl()).matches("^[0-9a-f\\-]{36}$"));
            assertTrue(extractCallbackId(params.getCancelUrl()).matches("^[0-9a-f\\-]{36}$"));
        }
    }

    @Test
    void paymentSuccessRejectsUnknownCallbackId() {
        ResponseEntity<String> response = paymentService.paymentSuccess("missing-callback");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid or expired payment callback", response.getBody());
    }

    private String registerCallbackForOrder(Order order, boolean successCallback) {
        order.setTotalQuantity(1);
        order.setTotalPrice(10.0);
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));

        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            Session session = new Session();
            session.setUrl("http://checkout");
            ArgumentCaptor<SessionCreateParams> paramsCaptor = ArgumentCaptor.forClass(SessionCreateParams.class);
            mocked.when(() -> Session.create(org.mockito.ArgumentMatchers.any(SessionCreateParams.class)))
                    .thenReturn(session);
            paymentService.createCheckoutSession(order.getId());
            mocked.verify(() -> Session.create(paramsCaptor.capture()));
            String url = successCallback ? paramsCaptor.getValue().getSuccessUrl() : paramsCaptor.getValue().getCancelUrl();
            return extractCallbackId(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String captureCallbackIdFromCheckoutUrl(Order order, boolean successCallback) {
        return registerCallbackForOrder(order, successCallback);
    }

    private String extractCallbackId(String url) {
        String prefix = "callbackId=";
        int start = url.indexOf(prefix);
        return url.substring(start + prefix.length());
    }
}
