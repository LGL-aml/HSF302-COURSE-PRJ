package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

}
