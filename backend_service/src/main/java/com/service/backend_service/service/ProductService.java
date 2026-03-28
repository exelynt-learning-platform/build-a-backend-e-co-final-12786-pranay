package com.service.backend_service.service;

import com.service.backend_service.dto.ProductDto;
import com.service.backend_service.model.Product;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    ResponseEntity<Product> addProduct(ProductDto productDto);

    ResponseEntity<Product> getProduct(Long productId);

    ResponseEntity<List<Product>> getAllProducts();

    ResponseEntity<Product> updateProduct(Long productId, ProductDto productDto);

    ResponseEntity<String> deleteProduct(Long productId);
}
