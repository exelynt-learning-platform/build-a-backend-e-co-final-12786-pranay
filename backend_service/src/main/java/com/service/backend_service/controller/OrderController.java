package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.model.Order;
import com.service.backend_service.service.OrderService;
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
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;
    private final ResponseHelper responseHelper;

    public OrderController(OrderService orderService, ResponseHelper responseHelper) {
        this.orderService = orderService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Order>> addOrder(@Valid @RequestBody OrderDto orderDto) {
        ResponseEntity<Order> response = orderService.addOrder(orderDto);
        return responseHelper.build(response, "Order created successfully");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long orderId) {
        ResponseEntity<Order> response = orderService.getOrder(orderId);
        return responseHelper.build(response, "Order fetched successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        ResponseEntity<List<Order>> response = orderService.getAllOrders();
        return responseHelper.build(response, "Orders fetched successfully");
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<ApiResponse<Order>> updateOrder(@PathVariable Long orderId,
                                                            @RequestBody OrderDto orderDto) {
        ResponseEntity<Order> response = orderService.updateOrder(orderId, orderDto);
        return responseHelper.build(response, "Order updated successfully");
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long orderId) {
        ResponseEntity<String> response = orderService.deleteOrder(orderId);
        return responseHelper.build(response, "Order deleted successfully");
    }
}
