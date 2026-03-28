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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

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
        when(paymentService.paymentSuccess(1L)).thenReturn(ResponseEntity.ok("Payment Successful"));

        mockMvc.perform(get("/payment/success").param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment Successful"));
    }
}
