package com.service.backend_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.service.backend_service.enums.OrderStatus;
import com.service.backend_service.enums.PaymentStatus;
import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
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
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Enumerated
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Enumerated
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "shipping_details")
    private String shippingDetails;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "total_price")
    private Double totalPrice;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_cart_id",referencedColumnName = "id")
    @JsonIgnore
    private Cart cart;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JsonIgnore
    private Set<Product> products = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id",referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @JsonProperty("cartId")
    public Long getCartId() {
        return cart != null ? cart.getId() : null;
    }

    @Transient
    public Product getProduct() {
        return products.stream().findFirst().orElse(null);
    }

    public void setProduct(Product product) {
        products.clear();
        if (product != null) {
            products.add(product);
        }
    }

    @JsonProperty("productId")
    public Long getProductId() {
        Product product = getProduct();
        return product != null ? product.getId() : null;
    }

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }


//    @Column(name = "fk_cart_id")
//    private Long fk_cart_id;
//
//    @Column(name = "fk_user_id")
//    private Long fk_user_id;
//
//    @Column(name = "fk_product_id")
//    private Long fk_product_id;


}
