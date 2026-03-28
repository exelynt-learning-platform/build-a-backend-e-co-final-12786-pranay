package com.service.backend_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @Column(name = "fk_user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_id", referencedColumnName = "id")
    @JsonIgnore
    private Product product;

    @Column(name = "fk_product_id", insertable = false, updatable = false)
    private Long productId;

    @JsonProperty("userId")
    public Long getResolvedUserId() {
        return userId;
    }

    @JsonProperty("productId")
    public Long getResolvedProductId() {
        return productId;
    }
}
