package com.service.backend_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.service.backend_service.model.Cart;
import com.service.backend_service.service.CartService;
import org.springframework.context.support.StaticMessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    private CartController cartController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("response.request_failed", java.util.Locale.getDefault(), "Request failed");
        messageSource.addMessage("response.not_found", java.util.Locale.getDefault(), "Resource not found");
        messageSource.addMessage("response.bad_request", java.util.Locale.getDefault(), "Invalid request");
        messageSource.addMessage("response.insufficient_storage", java.util.Locale.getDefault(), "Requested quantity is unavailable");
        cartController = new CartController(cartService, new ResponseHelper(messageSource));
    }

    @Test
    void addCartReturnsWrappedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(2);
        when(cartService.addCart(org.mockito.ArgumentMatchers.any())).thenReturn(ResponseEntity.ok(cart));

        mockMvc.perform(post("/carts/add")
                        .contentType("application/json")
                        .content("{\"quantity\":2,\"userId\":1,\"productId\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart created successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void updateCartReturnsInsufficientStockMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        when(cartService.updateCart(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE));

        mockMvc.perform(put("/carts/update/1")
                        .contentType("application/json")
                        .content("{\"quantity\":10}"))
                .andExpect(status().isInsufficientStorage())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Requested quantity is unavailable"));
    }

    @Test
    void deleteCartReturnsServiceMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        when(cartService.deleteCartItem(1L, 3L)).thenReturn(ResponseEntity.ok("Product removed from cart and cart deleted successfully"));

        mockMvc.perform(delete("/carts/delete/1/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product removed from cart and cart deleted successfully"));
    }
}
