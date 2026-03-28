package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import com.service.backend_service.dto.CartDto;
import com.service.backend_service.model.Cart;
import com.service.backend_service.service.CartService;
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
@RequestMapping("carts/")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Cart>> addCart(@Valid @RequestBody CartDto cartDto) {
        ResponseEntity<Cart> response = cartService.addCart(cartDto);
        return ResponseHelper.build(
                response,
                "Cart created successfully",
                "Cart not found",
                "Unable to create cart with the provided details",
                "Requested quantity is not available"
        );
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Cart>> getCart(@PathVariable Long cartId) {
        ResponseEntity<Cart> response = cartService.getCart(cartId);
        return ResponseHelper.build(
                response,
                "Cart fetched successfully",
                "Cart not found",
                "Invalid cart id",
                "Cart stock information is unavailable"
        );
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Cart>>> getAllCarts() {
        ResponseEntity<List<Cart>> response = cartService.getAllCarts();
        return ResponseHelper.build(
                response,
                "Carts fetched successfully",
                "Carts not found",
                "Unable to fetch carts",
                "Cart stock information is unavailable"
        );
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity<ApiResponse<Cart>> updateCart(@PathVariable Long cartId,
                                                         @RequestBody CartDto cartDto) {
        ResponseEntity<Cart> response = cartService.updateCart(cartId, cartDto);
        return ResponseHelper.build(
                response,
                "Cart updated successfully",
                "Cart not found",
                "Unable to update cart with the provided details",
                "Requested quantity is not available"
        );
    }

    @DeleteMapping("/delete/{cartId}/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteCart(@PathVariable Long cartId,
                                                           @PathVariable Long productId) {
        ResponseEntity<String> response = cartService.deleteCart(cartId, productId);
        return ResponseHelper.build(
                response,
                "Cart deleted successfully",
                "Cart not found",
                "Unable to delete the requested cart item",
                "Requested cart item is not available"
        );
    }
}
