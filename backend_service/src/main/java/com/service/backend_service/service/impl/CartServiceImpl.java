package com.service.backend_service.service.impl;

import com.service.backend_service.dto.CartDto;
import com.service.backend_service.model.Cart;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.CartService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<Cart> addCart(CartDto cartDto) {
        User user = userRepository.findById(cartDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(cartDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStockQuantity() < cartDto.getQuantity()) {
            return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);
        }
        Cart cart = new Cart();
        cart.setQuantity(cartDto.getQuantity());
        cart.setUser(user);
        cart.setProduct(product);

        Cart savedCart = cartRepository.save(cart);
        return ResponseEntity.ok(savedCart);
    }

    @Override
    public ResponseEntity<Cart> getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Cart>> getAllCarts() {
        return ResponseEntity.ok(cartRepository.findAll());
    }

    @Override
    public ResponseEntity<Cart> updateCart(Long cartId, CartDto cartDto) {
        return cartRepository.findById(cartId)
                .map(existingCart -> {
                    Product product = existingCart.getProduct();

                    if (cartDto.getProductId() != null) {
                        product = productRepository.findById(cartDto.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                        existingCart.setProduct(product);
                    }
                    if (cartDto.getQuantity() != null) {
                        if (product == null || product.getStockQuantity() == null) {
                            return new ResponseEntity<Cart>(HttpStatus.BAD_REQUEST);
                        }

                        int updatedQuantity = existingCart.getQuantity() + cartDto.getQuantity();
                        if (updatedQuantity > product.getStockQuantity()) {
                            return new ResponseEntity<Cart>(HttpStatus.INSUFFICIENT_STORAGE);
                        }

                        existingCart.setQuantity(updatedQuantity);
                    }
                    if (cartDto.getUserId() != null) {
                        User user = userRepository.findById(cartDto.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        existingCart.setUser(user);
                    }

                    Cart updatedCart = cartRepository.save(existingCart);
                    return ResponseEntity.ok(updatedCart);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<String> deleteCart(Long cartId, Long productId) {
        return cartRepository.findById(cartId)
                .map(cart -> {
                    if (cart.getProduct() == null) {
                        return ResponseEntity.badRequest().body("No product found in cart");
                    }
                    if (!cart.getProduct().getId().equals(productId)) {
                        return ResponseEntity.badRequest().body("Selected product is not present in this cart");
                    }

                    cartRepository.delete(cart);
                    return ResponseEntity.ok("Product removed from cart and cart deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
