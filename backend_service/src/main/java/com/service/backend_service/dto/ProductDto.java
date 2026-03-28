package com.service.backend_service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto implements Serializable {
    @NotNull
    private String name;

    @NotNull
    private String imageUrl;

    @NotNull
    private String description;

    @NotNull
    private Integer stockQuantity;

    @NotNull
    private Double price;
}
