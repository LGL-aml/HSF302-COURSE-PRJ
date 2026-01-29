package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Order;
import com.jungle.courseshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, String> {
    List<Order> findByStatusAndPaidAtBetween(OrderStatus status, LocalDateTime from, LocalDateTime to);
}

