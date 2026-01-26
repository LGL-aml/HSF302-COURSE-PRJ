package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, String> {
}
