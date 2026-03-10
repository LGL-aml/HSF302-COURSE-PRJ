package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.OrderItem;
import com.jungle.courseshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    // Top best-selling courses in a period
    @Query("SELECT oi.course.id, oi.course.title, oi.course.creator.fullname, COUNT(oi.id), SUM(oi.price), oi.course.price " +
           "FROM OrderItem oi " +
           "WHERE oi.order.status = :status AND oi.order.paidAt BETWEEN :from AND :to " +
           "GROUP BY oi.course.id, oi.course.title, oi.course.creator.fullname, oi.course.price " +
           "ORDER BY COUNT(oi.id) DESC")
    List<Object[]> findBestSellingCourses(@Param("status") OrderStatus status,
                                          @Param("from") LocalDateTime from,
                                          @Param("to") LocalDateTime to);
}
