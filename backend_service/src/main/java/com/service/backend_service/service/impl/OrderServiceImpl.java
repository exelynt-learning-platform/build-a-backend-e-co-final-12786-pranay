package com.service.backend_service.service.impl;

import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Cart;
import com.service.backend_service.model.Orders;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.OrdersRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.OrderService;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public OrderServiceImpl(OrdersRepository ordersRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {
        this.ordersRepository = ordersRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<Orders> addOrder(OrderDto orderDto) {
        Cart cart = cartRepository.findById(orderDto.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product product = productRepository.findById(orderDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cart.getQuantity() <= 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Orders order = new Orders();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingDetails(orderDto.getShippingDetails());
        order.setTotalQuantity(orderDto.getTotalQuantity());
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setCart(cart);
        order.setProduct(product);
        order.setUser(user);

        Orders savedOrder = ordersRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    @Override
    public ResponseEntity<Orders> getOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(ordersRepository.findAll());
    }

    @Override
    public ResponseEntity<Orders> updateOrder(Long orderId, OrderDto orderDto) {
        return ordersRepository.findById(orderId)
                .map(existingOrder -> {
                    if (orderDto.getOrderStatus() != null) {
                        existingOrder.setOrderStatus(orderDto.getOrderStatus());
                    }
                    if (orderDto.getPaymentStatus() != null) {
                        existingOrder.setPaymentStatus(orderDto.getPaymentStatus());
                    }
                    if (orderDto.getShippingDetails() != null) {
                        existingOrder.setShippingDetails(orderDto.getShippingDetails());
                    }
                    if (orderDto.getTotalQuantity() != null) {
                        existingOrder.setTotalQuantity(orderDto.getTotalQuantity());
                    }
                    if (orderDto.getTotalPrice() != null) {
                        existingOrder.setTotalPrice(orderDto.getTotalPrice());
                    }
                    if (orderDto.getCartId() != null) {
                        Cart cart = cartRepository.findById(orderDto.getCartId())
                                .orElseThrow(() -> new RuntimeException("Cart not found"));
                        existingOrder.setCart(cart);
                    }
                    if (orderDto.getProductId() != null) {
                        Product product = productRepository.findById(orderDto.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                        existingOrder.setProduct(product);
                    }
                    if (orderDto.getUserId() != null) {
                        User user = userRepository.findById(orderDto.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        existingOrder.setUser(user);
                    }

                    Orders updatedOrder = ordersRepository.save(existingOrder);
                    return ResponseEntity.ok(updatedOrder);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<String> deleteOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .map(order -> {
                    ordersRepository.delete(order);
                    return ResponseEntity.ok("Order deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
