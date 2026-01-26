package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.CartItem;

import org.springframework.data.jpa.repository.JpaRepository;



public interface CartItemRepo extends JpaRepository<CartItem, Long> {

}
