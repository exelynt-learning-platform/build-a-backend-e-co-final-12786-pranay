package com.service.backend_service.service.impl;

import com.service.backend_service.dto.CartDto;
import com.service.backend_service.exception.ProductNotFoundException;
import com.service.backend_service.exception.UserNotFoundException;
import com.service.backend_service.model.Cart;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.CartService;
import com.service.backend_service.service.StockValidationService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    private final StockValidationService stockValidationService;

    public CartServiceImpl(CartRepository cartRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           StockValidationService stockValidationService) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockValidationService = stockValidationService;
    }

    @Override
    public ResponseEntity<Cart> addCart(CartDto cartDto) {
        User user = userRepository.findById(cartDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Product product = productRepository.findById(cartDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (!stockValidationService.hasSufficientStock(product, cartDto.getQuantity())) {
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
                    Product product = resolveUpdatedProduct(existingCart, cartDto);
                    ResponseEntity<Cart> quantityValidationResponse = applyUpdatedQuantity(existingCart, cartDto, product);
                    if (quantityValidationResponse != null) {
                        return quantityValidationResponse;
                    }
                    applyUpdatedUser(existingCart, cartDto);
                    return ResponseEntity.ok(cartRepository.save(existingCart));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Product resolveUpdatedProduct(Cart existingCart, CartDto cartDto) {
        if (cartDto.getProductId() == null) {
            return existingCart.getProduct();
        }

        Product updatedProduct = productRepository.findById(cartDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        existingCart.setProduct(updatedProduct);
        return updatedProduct;
    }

    private ResponseEntity<Cart> applyUpdatedQuantity(Cart existingCart, CartDto cartDto, Product product) {
        if (cartDto.getQuantity() == null) {
            return null;
        }
        if (product == null || product.getStockQuantity() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int existingQuantity = existingCart.getQuantity() == null ? 0 : existingCart.getQuantity();
        int updatedQuantity = existingQuantity + cartDto.getQuantity();
        if (updatedQuantity > product.getStockQuantity()) {
            return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);
        }

        existingCart.setQuantity(updatedQuantity);
        return null;
    }

    private void applyUpdatedUser(Cart existingCart, CartDto cartDto) {
        if (cartDto.getUserId() == null) {
            return;
        }

        User user = userRepository.findById(cartDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        existingCart.setUser(user);
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
