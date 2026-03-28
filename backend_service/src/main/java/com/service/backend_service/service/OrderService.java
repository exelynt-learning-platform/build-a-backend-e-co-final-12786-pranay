package com.service.backend_service.service;

import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.model.Orders;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<Orders> addOrder(OrderDto orderDto);

    ResponseEntity<Orders> getOrder(Long orderId);

    ResponseEntity<List<Orders>> getAllOrders();

    ResponseEntity<Orders> updateOrder(Long orderId, OrderDto orderDto);

    ResponseEntity<String> deleteOrder(Long orderId);
}
