package com.service.backend_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.service.backend_service.dto.ProductDto;
import com.service.backend_service.model.Product;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.service.impl.ProductServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void addProductSavesMappedEntity() {
        ProductDto dto = new ProductDto();
        dto.setName("Phone");
        Product mapped = new Product();
        mapped.setName("Phone");
        Product saved = new Product(1L, "Phone", "img", "desc", 4, 100.0);

        when(modelMapper.map(dto, Product.class)).thenReturn(mapped);
        when(productRepository.save(mapped)).thenReturn(saved);

        ResponseEntity<Product> response = productService.addProduct(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Phone", response.getBody().getName());
    }

    @Test
    void getProductReturnsNotFoundWhenMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productService.getProduct(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllProductsReturnsAllRows() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(1L, "Phone", "img", "desc", 4, 100.0)));

        ResponseEntity<List<Product>> response = productService.getAllProducts();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateProductOnlyUpdatesProvidedFields() {
        Product existing = new Product(1L, "Old", "old", "old-desc", 2, 50.0);
        ProductDto dto = new ProductDto();
        dto.setPrice(80.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Product> response = productService.updateProduct(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Old", response.getBody().getName());
        assertEquals(80.0, response.getBody().getPrice());
    }

    @Test
    void deleteProductDeletesEntity() {
        Product product = new Product(1L, "Phone", "img", "desc", 4, 100.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<String> response = productService.deleteProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
        verify(productRepository).delete(product);
    }
}
