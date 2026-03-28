package com.service.backend_service.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class EntityRelationshipTest {

    @Test
    void cartGetProductReturnsNullWhenProductIsMissing() {
        Cart cart = new Cart();

        assertNull(cart.getProduct());
    }

    @Test
    void ordersGetProductReturnsNullWhenProductIsMissing() {
        Orders order = new Orders();

        assertNull(order.getProduct());
    }

    @Test
    void cartUsesManyToOneRelationshipForUser() throws NoSuchFieldException {
        Field userField = Cart.class.getDeclaredField("user");

        assertTrue(userField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void ordersUsesManyToOneRelationshipForCart() throws NoSuchFieldException {
        Field cartField = Orders.class.getDeclaredField("cart");

        assertTrue(cartField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void userUsesOneToManyRelationshipForCarts() throws NoSuchFieldException {
        Field cartsField = User.class.getDeclaredField("carts");

        assertTrue(cartsField.isAnnotationPresent(OneToMany.class));
    }

    @Test
    void cartUsesManyToOneRelationshipForProduct() throws NoSuchFieldException {
        Field productField = Cart.class.getDeclaredField("product");

        assertTrue(productField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void ordersUsesManyToOneRelationshipForProduct() throws NoSuchFieldException {
        Field productField = Orders.class.getDeclaredField("product");

        assertTrue(productField.isAnnotationPresent(ManyToOne.class));
    }

    @Test
    void ordersUsesStringEnumMappingForStatuses() throws NoSuchFieldException {
        Field orderStatusField = Orders.class.getDeclaredField("orderStatus");
        Field paymentStatusField = Orders.class.getDeclaredField("paymentStatus");

        assertTrue(orderStatusField.isAnnotationPresent(Enumerated.class));
        assertTrue(paymentStatusField.isAnnotationPresent(Enumerated.class));
        assertTrue(orderStatusField.getAnnotation(Enumerated.class).value() == EnumType.STRING);
        assertTrue(paymentStatusField.getAnnotation(Enumerated.class).value() == EnumType.STRING);
    }
}
