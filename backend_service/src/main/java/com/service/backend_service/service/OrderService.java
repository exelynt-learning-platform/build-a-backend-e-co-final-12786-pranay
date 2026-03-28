package com.service.backend_service.service;

import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.model.Order;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<Order> addOrder(OrderDto orderDto);

    ResponseEntity<Order> getOrder(Long orderId);

    ResponseEntity<List<Order>> getAllOrders();

    ResponseEntity<Order> updateOrder(Long orderId, OrderDto orderDto);

    ResponseEntity<String> deleteOrder(Long orderId);
}
