package com.service.backend_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.service.backend_service.dto.CartDto;
import com.service.backend_service.exception.ProductNotFoundException;
import com.service.backend_service.exception.UserNotFoundException;
import com.service.backend_service.model.Cart;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.impl.CartServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void addCartReturnsInsufficientStorageWhenStockIsLow() {
        CartDto dto = new CartDto();
        dto.setUserId(1L);
        dto.setProductId(2L);
        dto.setQuantity(6);

        User user = new User();
        user.setId(1L);
        Product product = new Product(2L, "Phone", "img", "desc", 5, 100.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        ResponseEntity<Cart> response = cartService.addCart(dto);

        assertEquals(HttpStatus.INSUFFICIENT_STORAGE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateCartRejectsQuantityAboveAvailableStock() {
        Cart existing = new Cart();
        existing.setId(1L);
        existing.setQuantity(2);
        existing.setProduct(new Product(10L, "Phone", "img", "desc", 3, 100.0));

        CartDto dto = new CartDto();
        dto.setQuantity(2);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(existing));

        ResponseEntity<Cart> response = cartService.updateCart(1L, dto);

        assertEquals(HttpStatus.INSUFFICIENT_STORAGE, response.getStatusCode());
    }

    @Test
    void updateCartAddsQuantityWhenStockIsAvailable() {
        Cart existing = new Cart();
        existing.setId(1L);
        existing.setQuantity(2);
        existing.setProduct(new Product(10L, "Phone", "img", "desc", 10, 100.0));

        CartDto dto = new CartDto();
        dto.setQuantity(3);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Cart> response = cartService.updateCart(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getQuantity());
    }

    @Test
    void updateCartRejectsWhenProductStockIsMissing() {
        Cart existing = new Cart();
        existing.setId(1L);
        existing.setQuantity(2);
        existing.setProduct(new Product(10L, "Phone", "img", "desc", null, 100.0));

        CartDto dto = new CartDto();
        dto.setQuantity(1);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(existing));

        ResponseEntity<Cart> response = cartService.updateCart(1L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteCartRejectsWhenSelectedProductDoesNotMatch() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setProduct(new Product(10L, "Phone", "img", "desc", 10, 100.0));

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        ResponseEntity<String> response = cartService.deleteCart(1L, 11L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Selected product is not present in this cart", response.getBody());
    }

    @Test
    void deleteCartDeletesRowWhenProductMatches() {
        Cart cart = new Cart();
        cart.setId(1L);
        Product product = new Product(10L, "Phone", "img", "desc", 10, 100.0);
        cart.setProduct(product);
        cart.setQuantity(1);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = cartService.deleteCart(1L, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product removed from cart successfully", response.getBody());
        assertEquals(0, cart.getQuantity());
        assertNull(cart.getProduct());
        verify(cartRepository).save(cart);
    }

    @Test
    void addCartThrowsUserNotFoundExceptionWhenUserIsMissing() {
        CartDto dto = new CartDto();
        dto.setUserId(1L);
        dto.setProductId(2L);
        dto.setQuantity(1);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cartService.addCart(dto));
    }

    @Test
    void updateCartThrowsProductNotFoundExceptionWhenUpdatedProductIsMissing() {
        Cart existing = new Cart();
        existing.setId(1L);
        existing.setQuantity(2);
        existing.setProduct(new Product(10L, "Phone", "img", "desc", 10, 100.0));

        CartDto dto = new CartDto();
        dto.setProductId(99L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartService.updateCart(1L, dto));
    }
}
