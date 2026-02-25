package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);
}
