package com.service.backend_service.dto;

import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto implements Serializable {
    @NotNull
    private OrderStatus orderStatus;

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    private String shippingDetails;

    @NotNull
    private Integer totalQuantity;

    @NotNull
    private Double totalPrice;

    @NotNull
    private Long cartId;

    @NotNull
    private Long productId;

    @NotNull
    private Long userId;
}
