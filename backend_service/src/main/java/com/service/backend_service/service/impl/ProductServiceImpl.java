package com.service.backend_service.service.impl;

import com.service.backend_service.dto.ProductDto;
import com.service.backend_service.model.Product;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.service.ProductService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<Product> addProduct(ProductDto productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @Override
    public ResponseEntity<Product> getProduct(Long productId) {
        return productRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @Override
    public ResponseEntity<Product> updateProduct(Long productId, ProductDto productDto) {
        return productRepository.findById(productId)
                .map(existingProduct -> {
                    if (productDto.getName() != null) {
                        existingProduct.setName(productDto.getName());
                    }
                    if (productDto.getImageUrl() != null) {
                        existingProduct.setImageUrl(productDto.getImageUrl());
                    }
                    if (productDto.getDescription() != null) {
                        existingProduct.setDescription(productDto.getDescription());
                    }
                    if (productDto.getStockQuantity() != null) {
                        existingProduct.setStockQuantity(productDto.getStockQuantity());
                    }
                    if (productDto.getPrice() != null) {
                        existingProduct.setPrice(productDto.getPrice());
                    }
                    Product updatedProduct = productRepository.save(existingProduct);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<String> deleteProduct(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    productRepository.delete(product);
                    return ResponseEntity.ok("Product deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
