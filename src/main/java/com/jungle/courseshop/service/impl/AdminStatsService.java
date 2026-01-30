package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.response.AdminStatsResponse;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final TopicRepo topicRepo;
    private final OrderRepo orderRepo;

    public AdminStatsResponse getDashboardStats(String period) {
        // Xác định khoảng thời gian (30 ngày gần nhất hoặc 12 tháng gần nhất)
        boolean isMonthly = "year".equals(period);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = isMonthly 
            ? endDate.minusMonths(12).withDayOfMonth(1) 
            : endDate.minusDays(30);

        // Lấy dữ liệu thống kê
        long totalUsers = userRepo.countByEnabledTrue();
        long totalCourses = courseRepo.count();
        long totalTopics = topicRepo.count();
        
        // Tính tổng doanh thu và đơn hàng đã thanh toán
        List<Order> paidOrders = orderRepo.findByStatusAndPaidAtBetween(
            OrderStatus.PAID, startDate, endDate);
        
        BigDecimal totalRevenue = paidOrders.stream()
            .map(Order::getTotalAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy top 5 khóa học có nhiều đăng ký nhất
        List<AdminStatsResponse.TopCourseStat> topCourses = courseRepo.findTop5ByActiveTrueOrderByEnrolledCountDesc()
            .stream()
            .map(course -> new AdminStatsResponse.TopCourseStat(
                course.getId(),
                course.getTitle(),
                course.getEnrolledCount()
            ))
            .collect(Collectors.toList());

        // Tạo dữ liệu cho biểu đồ
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenueSeries = new ArrayList<>();
        List<Long> orderSeries = new ArrayList<>();

        if (isMonthly) {
            // Thống kê theo tháng (12 tháng gần nhất)
            LocalDate current = LocalDate.now().withDayOfMonth(1);
            for (int i = 11; i >= 0; i--) {
                YearMonth month = YearMonth.from(current.minusMonths(i));
                labels.add(month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                
                // Lọc đơn hàng trong tháng
                LocalDateTime monthStart = month.atDay(1).atStartOfDay();
                LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
                
                List<Order> monthOrders = orderRepo.findByStatusAndPaidAtBetween(
                    OrderStatus.PAID, monthStart, monthEnd);
                
                // Tính doanh thu tháng
                BigDecimal monthRevenue = monthOrders.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                revenueSeries.add(monthRevenue);
                orderSeries.add((long) monthOrders.size());
            }
        } else {
            // Thống kê theo ngày (30 ngày gần nhất)
            LocalDate current = LocalDate.now();
            for (int i = 29; i >= 0; i--) {
                LocalDate day = current.minusDays(i);
                labels.add(day.format(DateTimeFormatter.ofPattern("dd/MM")));
                
                // Lọc đơn hàng trong ngày
                LocalDateTime dayStart = day.atStartOfDay();
                LocalDateTime dayEnd = day.atTime(23, 59, 59);
                
                List<Order> dayOrders = orderRepo.findByStatusAndPaidAtBetween(
                    OrderStatus.PAID, dayStart, dayEnd);
                
                // Tính doanh thu ngày
                BigDecimal dayRevenue = dayOrders.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                revenueSeries.add(dayRevenue);
                orderSeries.add((long) dayOrders.size());
            }
        }

        return new AdminStatsResponse(
            totalUsers,
            totalCourses,
            totalTopics,
            paidOrders.size(),
            totalRevenue,
            labels,
            revenueSeries,
            orderSeries,
            topCourses
        );
    }
}