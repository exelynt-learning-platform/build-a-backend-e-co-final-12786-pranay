package com.service.backend_service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDto implements Serializable {
    @NotNull
    private Integer quantity;

    @NotNull
    private Long userId;

    @NotNull
    private Long productId;
}
