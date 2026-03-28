package com.service.backend_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.service.backend_service.service.PriceCalculationService;
import com.service.backend_service.service.impl.OrderServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PriceCalculationService priceCalculationService;

    @Mock
    private StockValidationService stockValidationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void addOrderCreatesPendingOrder() {
        OrderDto dto = new OrderDto();
        dto.setCartId(1L);
        dto.setProductId(2L);
        dto.setUserId(3L);
        dto.setShippingDetails("Pune");
        dto.setTotalQuantity(2);
        dto.setTotalPrice(200.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(2);
        Product product = new Product(2L, "Phone", "img", "desc", 10, 100.0);
        User user = new User();
        user.setId(3L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(stockValidationService.hasSufficientStock(product, 2)).thenReturn(true);
        when(priceCalculationService.calculateTotalPrice(2, 100.0)).thenReturn(new java.math.BigDecimal("200.00"));
        when(priceCalculationService.matchesExpectedTotal(200.0, new java.math.BigDecimal("200.00"))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Order> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.PENDING, response.getBody().getOrderStatus());
        assertEquals(PaymentStatus.PENDING, response.getBody().getPaymentStatus());
        assertEquals(200.0, response.getBody().getTotalPrice());
    }

    @Test
    void addOrderRejectsEmptyCart() {
        OrderDto dto = new OrderDto();
        dto.setCartId(1L);
        dto.setProductId(2L);
        dto.setUserId(3L);
        dto.setShippingDetails("Pune");
        dto.setTotalQuantity(2);
        dto.setTotalPrice(200.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(0);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User()));

        ResponseEntity<Order> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addOrderRejectsCartQuantityAboveProductStock() {
        OrderDto dto = new OrderDto();
        dto.setCartId(1L);
        dto.setProductId(2L);
        dto.setUserId(3L);
        dto.setShippingDetails("Pune");
        dto.setTotalQuantity(4);
        dto.setTotalPrice(400.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(4);
        Product product = new Product(2L, "Phone", "img", "desc", 3, 100.0);
        User user = new User();
        user.setId(3L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(stockValidationService.hasSufficientStock(product, 4)).thenReturn(false);

        ResponseEntity<Order> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addOrderRejectsWhenProductPriceIsMissing() {
        OrderDto dto = new OrderDto();
        dto.setCartId(1L);
        dto.setProductId(2L);
        dto.setUserId(3L);
        dto.setShippingDetails("Pune");
        dto.setTotalQuantity(2);
        dto.setTotalPrice(200.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(2);
        Product product = new Product(2L, "Phone", "img", "desc", 10, null);
        User user = new User();
        user.setId(3L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        ResponseEntity<Order> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addOrderRejectsWhenProvidedTotalPriceDoesNotMatchCalculatedPrice() {
        OrderDto dto = new OrderDto();
        dto.setCartId(1L);
        dto.setProductId(2L);
        dto.setUserId(3L);
        dto.setShippingDetails("Pune");
        dto.setTotalQuantity(2);
        dto.setTotalPrice(150.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(2);
        Product product = new Product(2L, "Phone", "img", "desc", 10, 100.0);
        User user = new User();
        user.setId(3L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(stockValidationService.hasSufficientStock(product, 2)).thenReturn(true);
        when(priceCalculationService.calculateTotalPrice(2, 100.0)).thenReturn(new java.math.BigDecimal("200.00"));
        when(priceCalculationService.matchesExpectedTotal(150.0, new java.math.BigDecimal("200.00"))).thenReturn(false);

        ResponseEntity<Order> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addOrderAcceptsRoundedCurrencyEquivalentTotalPrice() {
        OrderDto dto = new OrderDto();
        dto.setCartId(1L);
        dto.setProductId(2L);
        dto.setUserId(3L);
        dto.setShippingDetails("Pune");
        dto.setTotalQuantity(3);
        dto.setTotalPrice(29.97);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(3);
        Product product = new Product(2L, "Phone", "img", "desc", 10, 9.99);
        User user = new User();
        user.setId(3L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(stockValidationService.hasSufficientStock(product, 3)).thenReturn(true);
        when(priceCalculationService.calculateTotalPrice(3, 9.99)).thenReturn(new java.math.BigDecimal("29.97"));
        when(priceCalculationService.matchesExpectedTotal(29.97, new java.math.BigDecimal("29.97"))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Order> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(29.97, response.getBody().getTotalPrice());
    }

    @Test
    void getAllOrdersReturnsAllRows() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));

        ResponseEntity<List<Order>> response = orderService.getAllOrders();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateOrderUpdatesProvidedFields() {
        Order existing = new Order();
        existing.setId(1L);
        existing.setShippingDetails("Old");

        OrderDto dto = new OrderDto();
        dto.setShippingDetails("New");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Order> response = orderService.updateOrder(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New", response.getBody().getShippingDetails());
    }

    @Test
    void deleteOrderDeletesEntity() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<String> response = orderService.deleteOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository).delete(order);
    }
}
