package com.service.backend_service.service.impl;

import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Cart;
import com.service.backend_service.model.Order;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.OrdersRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.OrderService;
import com.service.backend_service.service.StockValidationService;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;
    private final StockValidationService stockValidationService;

    public OrderServiceImpl(OrdersRepository ordersRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            StockValidationService stockValidationService) {
        this.ordersRepository = ordersRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.stockValidationService = stockValidationService;
    }

    @Override
    public ResponseEntity<Order> addOrder(OrderDto orderDto) {
        Cart cart = cartRepository.findById(orderDto.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product product = productRepository.findById(orderDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer cartQuantity = extractValidCartQuantity(cart);
        if (cartQuantity == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!stockValidationService.hasSufficientStock(product, cartQuantity)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        double calculatedTotalPrice = cartQuantity * product.getPrice();
        if (orderDto.getTotalPrice() == null || Math.abs(orderDto.getTotalPrice() - calculatedTotalPrice) > 0.01d) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingDetails(orderDto.getShippingDetails());
        order.setTotalQuantity(orderDto.getTotalQuantity());
        order.setTotalPrice(calculatedTotalPrice);
        order.setCart(cart);
        order.setProduct(product);
        order.setUser(user);

        Order savedOrder = ordersRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    private Integer extractValidCartQuantity(Cart cart) {
        if (cart == null || cart.getQuantity() == null || cart.getQuantity() <= 0) {
            return null;
        }
        return cart.getQuantity();
    }

    @Override
    public ResponseEntity<Order> getOrder(Long orderId) {
        return ordersRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(ordersRepository.findAll());
    }

    @Override
    public ResponseEntity<Order> updateOrder(Long orderId, OrderDto orderDto) {
        return ordersRepository.findById(orderId)
                .map(existingOrder -> {
                    applyScalarUpdates(existingOrder, orderDto);
                    applyRelatedEntityUpdates(existingOrder, orderDto);
                    return ResponseEntity.ok(ordersRepository.save(existingOrder));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void applyScalarUpdates(Order existingOrder, OrderDto orderDto) {
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
    }

    private void applyRelatedEntityUpdates(Order existingOrder, OrderDto orderDto) {
        if (orderDto.getCartId() != null) {
            existingOrder.setCart(findCart(orderDto.getCartId()));
        }
        if (orderDto.getProductId() != null) {
            existingOrder.setProduct(findProduct(orderDto.getProductId()));
        }
        if (orderDto.getUserId() != null) {
            existingOrder.setUser(findUser(orderDto.getUserId()));
        }
    }

    private Cart findCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
