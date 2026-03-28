package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import com.service.backend_service.dto.OrderDto;
import com.service.backend_service.model.Orders;
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
@RequestMapping("orders/")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    private ResponseEntity<ApiResponse<Orders>> addOrder(@Valid @RequestBody OrderDto orderDto) {
        ResponseEntity<Orders> response = orderService.addOrder(orderDto);
        return ResponseHelper.build(
                response,
                "Order created successfully",
                "Order not found",
                "Invalid order request",
                "Requested quantity is not available"
        );
    }

    @GetMapping("/{orderId}")
    private ResponseEntity<ApiResponse<Orders>> getOrder(@PathVariable Long orderId) {
        ResponseEntity<Orders> response = orderService.getOrder(orderId);
        return ResponseHelper.build(
                response,
                "Order fetched successfully",
                "Order not found",
                "Invalid order request",
                "Requested quantity is not available"
        );
    }

    @GetMapping("/all")
    private ResponseEntity<ApiResponse<List<Orders>>> getAllOrders() {
        ResponseEntity<List<Orders>> response = orderService.getAllOrders();
        return ResponseHelper.build(
                response,
                "Orders fetched successfully",
                "Orders not found",
                "Invalid order request",
                "Requested quantity is not available"
        );
    }

    @PutMapping("/update/{orderId}")
    private ResponseEntity<ApiResponse<Orders>> updateOrder(@PathVariable Long orderId,
                                                            @RequestBody OrderDto orderDto) {
        ResponseEntity<Orders> response = orderService.updateOrder(orderId, orderDto);
        return ResponseHelper.build(
                response,
                "Order updated successfully",
                "Order not found",
                "Invalid order request",
                "Requested quantity is not available"
        );
    }

    @DeleteMapping("/delete/{orderId}")
    private ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long orderId) {
        ResponseEntity<String> response = orderService.deleteOrder(orderId);
        return ResponseHelper.build(
                response,
                "Order deleted successfully",
                "Order not found",
                "Invalid order request",
                "Requested quantity is not available"
        );
    }
}
