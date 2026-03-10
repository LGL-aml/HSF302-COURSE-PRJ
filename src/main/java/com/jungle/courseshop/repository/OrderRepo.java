package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Order;
import com.jungle.courseshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, String> {
    List<Order> findByStatusAndPaidAtBetween(OrderStatus status, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.paidAt BETWEEN :from AND :to")
    long countByStatusAndPaidAtBetween(@Param("status") OrderStatus status,
                                       @Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);
}

