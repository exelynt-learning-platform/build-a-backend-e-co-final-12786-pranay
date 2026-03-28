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
import com.service.backend_service.model.Orders;
import com.service.backend_service.model.Product;
import com.service.backend_service.model.User;
import com.service.backend_service.repo.CartRepository;
import com.service.backend_service.repo.OrdersRepository;
import com.service.backend_service.repo.ProductRepository;
import com.service.backend_service.repo.UserRepository;
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
    private OrdersRepository ordersRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

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
        when(ordersRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Orders> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.PENDING, response.getBody().getOrderStatus());
        assertEquals(PaymentStatus.PENDING, response.getBody().getPaymentStatus());
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

        ResponseEntity<Orders> response = orderService.addOrder(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllOrdersReturnsAllRows() {
        when(ordersRepository.findAll()).thenReturn(List.of(new Orders()));

        ResponseEntity<List<Orders>> response = orderService.getAllOrders();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateOrderUpdatesProvidedFields() {
        Orders existing = new Orders();
        existing.setId(1L);
        existing.setShippingDetails("Old");

        OrderDto dto = new OrderDto();
        dto.setShippingDetails("New");

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ordersRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Orders> response = orderService.updateOrder(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New", response.getBody().getShippingDetails());
    }

    @Test
    void deleteOrderDeletesEntity() {
        Orders order = new Orders();
        order.setId(1L);
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        ResponseEntity<String> response = orderService.deleteOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ordersRepository).delete(order);
    }
}
