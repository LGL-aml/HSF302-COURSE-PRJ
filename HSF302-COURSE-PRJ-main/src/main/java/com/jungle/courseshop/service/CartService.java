package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.CartReponse;
import com.jungle.courseshop.entity.Cart;

public interface CartService {
    CartReponse getCartByUser();

    Cart addItemToCart(Long courseId);

    void removeItemFromCart(Long userId, Long courseId);

    void removeItemFromCart(Long courseId);

    void clearCart();
}
