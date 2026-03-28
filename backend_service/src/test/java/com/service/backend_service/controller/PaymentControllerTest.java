package com.service.backend_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.service.backend_service.service.PaymentService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private PaymentController paymentController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        paymentController = new PaymentController(paymentService);
    }

    @Test
    void checkoutReturnsCheckoutUrl() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        when(paymentService.createCheckoutSession(1L)).thenReturn(ResponseEntity.ok(Map.of("checkoutUrl", "http://checkout")));

        mockMvc.perform(post("/payment/checkout/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl").value("http://checkout"));
    }

    @Test
    void paymentSuccessReturnsMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        when(paymentService.paymentSuccess("callback-123")).thenReturn(ResponseEntity.ok("Payment Successful"));

        mockMvc.perform(get("/payment/success").param("callbackId", "callback-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment Successful"));
    }

    @Test
    void paymentSuccessReturnsUnauthorizedForInvalidCallbackId() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        when(paymentService.paymentSuccess("expired-callback"))
                .thenReturn(ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired payment callback"));

        mockMvc.perform(get("/payment/success").param("callbackId", "expired-callback"))
                .andExpect(status().isUnauthorized());
    }
}
