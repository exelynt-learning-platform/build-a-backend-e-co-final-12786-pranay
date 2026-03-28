package com.service.backend_service.service;

import com.service.backend_service.dto.CartDto;
import com.service.backend_service.model.Cart;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<Cart> addCart(CartDto cartDto);

    ResponseEntity<Cart> getCart(Long cartId);

    ResponseEntity<List<Cart>> getAllCarts();

    ResponseEntity<Cart> updateCart(Long cartId, CartDto cartDto);

    ResponseEntity<String> deleteCart(Long cartId, Long productId);
}
