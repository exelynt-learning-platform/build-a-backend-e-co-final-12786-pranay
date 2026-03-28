package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import com.service.backend_service.dto.ProductDto;
import com.service.backend_service.model.Product;
import com.service.backend_service.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products/")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Product>> addProduct(@Valid @RequestBody ProductDto productDto) {
        ResponseEntity<Product> response = productService.addProduct(productDto);
        return ResponseHelper.build(response, "Product added successfully");
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long productId) {
        ResponseEntity<Product> response = productService.getProduct(productId);
        return ResponseHelper.build(response, "Product fetched successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        ResponseEntity<List<Product>> response = productService.getAllProducts();
        return ResponseHelper.build(response, "Products fetched successfully");
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long productId,
                                                               @RequestBody ProductDto productDto) {
        ResponseEntity<Product> response = productService.updateProduct(productId, productDto);
        return ResponseHelper.build(response, "Product updated successfully");
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long productId) {
        ResponseEntity<String> response = productService.deleteProduct(productId);
        return ResponseHelper.build(response, "Product deleted successfully");
    }
}
