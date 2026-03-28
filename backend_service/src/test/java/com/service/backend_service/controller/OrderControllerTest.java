package com.service.backend_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.service.backend_service.model.Orders;
import com.service.backend_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void addOrderReturnsWrappedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        Orders order = new Orders();
        order.setId(1L);
        when(orderService.addOrder(org.mockito.ArgumentMatchers.any())).thenReturn(ResponseEntity.ok(order));

        mockMvc.perform(post("/orders/add")
                        .contentType("application/json")
                        .content("{\"shippingDetails\":\"Pune\",\"totalQuantity\":2,\"totalPrice\":100,\"cartId\":1,\"productId\":1,\"userId\":1,\"orderStatus\":\"PENDING\",\"paymentStatus\":\"PENDING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getOrderReturnsNotFoundMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        when(orderService.getOrder(9L)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/orders/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }
}
