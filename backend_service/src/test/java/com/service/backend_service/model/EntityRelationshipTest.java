package com.service.backend_service.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class EntityRelationshipTest {

    @Test
    void cartGetProductReturnsNullWhenProductsSetIsNull() {
        Cart cart = new Cart();
        cart.setProducts(null);

        assertNull(cart.getProduct());
    }

    @Test
    void ordersGetProductReturnsNullWhenProductsSetIsNull() {
        Orders order = new Orders();
        order.setProducts(null);

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
}
