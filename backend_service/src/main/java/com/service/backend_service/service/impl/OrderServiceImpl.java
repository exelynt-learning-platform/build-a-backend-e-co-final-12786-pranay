package com.service.backend_service.service.impl;

import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import com.service.backend_service.model.Cart;
import com.service.backend_service.model.Order;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.OrderRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
import com.service.backend_service.service.OrderService;
import com.service.backend_service.service.PriceCalculationService;
import com.service.backend_service.service.StockValidationService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;
    private final PriceCalculationService priceCalculationService;
    private final StockValidationService stockValidationService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            PriceCalculationService priceCalculationService,
                            StockValidationService stockValidationService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.priceCalculationService = priceCalculationService;
        this.stockValidationService = stockValidationService;
    }

    @Override
    public ResponseEntity<Order> addOrder(OrderDto orderDto) {
        OrderCreationContext context = resolveOrderCreationContext(orderDto);
        if (!isValidOrderRequest(orderDto, context)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!stockValidationService.hasSufficientStock(context.product(), context.cartQuantity())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        BigDecimal calculatedTotalPrice =
                priceCalculationService.calculateTotalPrice(context.cartQuantity(), context.product().getPrice());
        if (!priceCalculationService.matchesExpectedTotal(orderDto.getTotalPrice(), calculatedTotalPrice)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Order order = buildOrder(orderDto, context, calculatedTotalPrice);
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    private OrderCreationContext resolveOrderCreationContext(OrderDto orderDto) {
        Cart cart = cartRepository.findById(orderDto.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product product = findProduct(orderDto.getProductId());
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Integer cartQuantity = extractValidCartQuantity(cart);
        return new OrderCreationContext(cart, product, user, cartQuantity);
    }

    private boolean isValidOrderRequest(OrderDto orderDto, OrderCreationContext context) {
        return context.cartQuantity() != null
                && context.product().getPrice() != null
                && orderDto.getTotalQuantity() != null;
    }

    private Order buildOrder(OrderDto orderDto, OrderCreationContext context, BigDecimal calculatedTotalPrice) {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingDetails(orderDto.getShippingDetails());
        order.setTotalQuantity(orderDto.getTotalQuantity());
        order.setTotalPrice(calculatedTotalPrice.doubleValue());
        order.setCart(context.cart());
        order.setProduct(context.product());
        order.setUser(context.user());
        return order;
    }

    private Integer extractValidCartQuantity(Cart cart) {
        if (cart == null || cart.getQuantity() == null || cart.getQuantity() <= 0) {
            return null;
        }
        return cart.getQuantity();
    }

    private record OrderCreationContext(Cart cart, Product product, User user, Integer cartQuantity) {
    }

    @Override
    public ResponseEntity<Order> getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @Override
    public ResponseEntity<Order> updateOrder(Long orderId, OrderDto orderDto) {
        return orderRepository.findById(orderId)
                .map(existingOrder -> {
                    applyScalarUpdates(existingOrder, orderDto);
                    applyRelatedEntityUpdates(existingOrder, orderDto);
                    return ResponseEntity.ok(orderRepository.save(existingOrder));
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
        return orderRepository.findById(orderId)
                .map(order -> {
                    orderRepository.delete(order);
                    return ResponseEntity.ok("Order deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
