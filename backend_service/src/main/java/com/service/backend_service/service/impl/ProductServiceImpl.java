package com.service.backend_service.service.impl;

import com.service.backend_service.dto.ProductDto;
import com.service.backend_service.model.Product;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.service.ProductService;
import java.util.List;
import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Condition<?, ?> NOT_NULL = Conditions.isNotNull();

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        configurePartialUpdateMapping(modelMapper);
    }

    @Override
    public ResponseEntity<Product> addProduct(ProductDto productDto) {
        if (!isValidProduct(productDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
        if (!isValidPartialProductUpdate(productDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return productRepository.findById(productId)
                .map(existingProduct -> {
                    modelMapper.map(productDto, existingProduct);
                    Product updatedProduct = productRepository.save(existingProduct);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void configurePartialUpdateMapping(ModelMapper mapper) {
        if (mapper.getTypeMap(ProductDto.class, Product.class) == null) {
            mapper.createTypeMap(ProductDto.class, Product.class).setPropertyCondition(NOT_NULL);
        } else {
            mapper.getTypeMap(ProductDto.class, Product.class).setPropertyCondition(NOT_NULL);
        }
    }

    private boolean isValidProduct(ProductDto productDto) {
        return productDto.getPrice() != null
                && productDto.getPrice() >= 0
                && productDto.getStockQuantity() != null
                && productDto.getStockQuantity() >= 0;
    }

    private boolean isValidPartialProductUpdate(ProductDto productDto) {
        return (productDto.getPrice() == null || productDto.getPrice() >= 0)
                && (productDto.getStockQuantity() == null || productDto.getStockQuantity() >= 0);
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
