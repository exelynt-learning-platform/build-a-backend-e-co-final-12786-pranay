package com.service.backend_service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.service.backend_service.model.Product;
import com.service.backend_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void addProductReturnsWrappedResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        when(productService.addProduct(org.mockito.ArgumentMatchers.any()))
                .thenReturn(ResponseEntity.ok(new Product(1L, "Phone", "img", "desc", 2, 100.0)));

        mockMvc.perform(post("/products/add")
                        .contentType("application/json")
                        .content("{\"name\":\"Phone\",\"imageUrl\":\"img\",\"description\":\"desc\",\"stockQuantity\":2,\"price\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product added successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getProductReturnsNotFoundMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        when(productService.getProduct(99L)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void deleteProductReturnsSuccessMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        when(productService.deleteProduct(1L)).thenReturn(ResponseEntity.ok("Product deleted successfully"));

        mockMvc.perform(delete("/products/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }
}
