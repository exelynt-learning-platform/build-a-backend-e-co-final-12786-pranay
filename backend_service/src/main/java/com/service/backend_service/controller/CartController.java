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
    private final ResponseHelper responseHelper;

    public CartController(CartService cartService, ResponseHelper responseHelper) {
        this.cartService = cartService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Cart>> createCart(@Valid @RequestBody CartDto cartDto) {
        ResponseEntity<Cart> response = cartService.addCart(cartDto);
        return responseHelper.build(response, "Cart created successfully");
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Cart>> getCart(@PathVariable Long cartId) {
        ResponseEntity<Cart> response = cartService.getCart(cartId);
        return responseHelper.build(response, "Cart fetched successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Cart>>> listCarts() {
        ResponseEntity<List<Cart>> response = cartService.getAllCarts();
        return responseHelper.build(response, "Carts fetched successfully");
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity<ApiResponse<Cart>> updateCart(@PathVariable Long cartId,
                                                         @RequestBody CartDto cartDto) {
        ResponseEntity<Cart> response = cartService.updateCart(cartId, cartDto);
        return responseHelper.build(response, "Cart updated successfully");
    }

    @DeleteMapping("/delete/{cartId}/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteCartItem(@PathVariable Long cartId,
                                                              @PathVariable Long productId) {
        ResponseEntity<String> response = cartService.deleteCart(cartId, productId);
        return responseHelper.build(response, "Cart deleted successfully");
    }
}
