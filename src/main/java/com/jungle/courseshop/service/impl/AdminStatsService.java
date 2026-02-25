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

    /**
     * Backward compatible method - uses period string only
     */
    public AdminStatsResponse getDashboardStats(String period) {
        return getDashboardStats(period, null, null);
    }

    /**
     * Enhanced method with custom date range support
     * @param period - "month", "year", "quarter", or "custom"
     * @param customStartDate - used when period is "custom"
     * @param customEndDate - used when period is "custom"
     */
    public AdminStatsResponse getDashboardStats(String period, LocalDate customStartDate, LocalDate customEndDate) {
        // Xác định khoảng thời gian
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();
        String periodType = period != null ? period : "month";
        
        // Handle custom date range
        if ("custom".equals(periodType) && customStartDate != null && customEndDate != null) {
            startDate = customStartDate.atStartOfDay();
            endDate = customEndDate.atTime(23, 59, 59);
        } else if ("year".equals(periodType)) {
            startDate = endDate.minusMonths(12).withDayOfMonth(1);
        } else if ("quarter".equals(periodType)) {
            startDate = endDate.minusMonths(12); // Last 4 quarters (12 months)
        } else {
            // Default to month (30 days)
            periodType = "month";
            startDate = endDate.minusDays(30);
        }

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
        List<AdminStatsResponse.QuarterlyComparison> quarterlyComparisons = new ArrayList<>();

        if ("quarter".equals(periodType)) {
            // Thống kê theo quý (4 quý gần nhất)
            quarterlyComparisons = generateQuarterlyData();
            
            // Generate chart data for quarterly view (show monthly data for last 12 months)
            LocalDate current = LocalDate.now().withDayOfMonth(1);
            for (int i = 11; i >= 0; i--) {
                YearMonth month = YearMonth.from(current.minusMonths(i));
                labels.add(month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                
                LocalDateTime monthStart = month.atDay(1).atStartOfDay();
                LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
                
                List<Order> monthOrders = orderRepo.findByStatusAndPaidAtBetween(
                    OrderStatus.PAID, monthStart, monthEnd);
                
                BigDecimal monthRevenue = monthOrders.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                revenueSeries.add(monthRevenue);
                orderSeries.add((long) monthOrders.size());
            }
        } else if ("year".equals(periodType)) {
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
        } else if ("custom".equals(periodType) && customStartDate != null && customEndDate != null) {
            // Thống kê theo custom date range
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(customStartDate, customEndDate);
            
            if (daysBetween <= 31) {
                // Hiển thị theo ngày nếu khoảng <= 31 ngày
                for (LocalDate date = customStartDate; !date.isAfter(customEndDate); date = date.plusDays(1)) {
                    labels.add(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    
                    LocalDateTime dayStart = date.atStartOfDay();
                    LocalDateTime dayEnd = date.atTime(23, 59, 59);
                    
                    List<Order> dayOrders = orderRepo.findByStatusAndPaidAtBetween(
                        OrderStatus.PAID, dayStart, dayEnd);
                    
                    BigDecimal dayRevenue = dayOrders.stream()
                        .map(Order::getTotalAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    revenueSeries.add(dayRevenue);
                    orderSeries.add((long) dayOrders.size());
                }
            } else {
                // Hiển thị theo tháng nếu khoảng > 31 ngày
                YearMonth startMonth = YearMonth.from(customStartDate);
                YearMonth endMonth = YearMonth.from(customEndDate);
                
                for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
                    labels.add(month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                    
                    LocalDateTime monthStart = month.atDay(1).atStartOfDay();
                    LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
                    
                    // Adjust for first and last month boundaries
                    if (month.equals(startMonth)) {
                        monthStart = customStartDate.atStartOfDay();
                    }
                    if (month.equals(endMonth)) {
                        monthEnd = customEndDate.atTime(23, 59, 59);
                    }
                    
                    List<Order> monthOrders = orderRepo.findByStatusAndPaidAtBetween(
                        OrderStatus.PAID, monthStart, monthEnd);
                    
                    BigDecimal monthRevenue = monthOrders.stream()
                        .map(Order::getTotalAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    revenueSeries.add(monthRevenue);
                    orderSeries.add((long) monthOrders.size());
                }
            }
        } else {
            // Thống kê theo ngày (30 ngày gần nhất) - default
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

        AdminStatsResponse response = new AdminStatsResponse(
            totalUsers,
            totalCourses,
            totalTopics,
            paidOrders.size(),
            totalRevenue,
            labels,
            revenueSeries,
            orderSeries,
            topCourses,
            quarterlyComparisons,
            periodType,
            customStartDate != null ? customStartDate : startDate.toLocalDate(),
            customEndDate != null ? customEndDate : endDate.toLocalDate()
        );
        
        return response;
    }
    
    /**
     * Generate quarterly comparison data for the last 4 quarters
     */
    private List<AdminStatsResponse.QuarterlyComparison> generateQuarterlyData() {
        List<AdminStatsResponse.QuarterlyComparison> comparisons = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        // Calculate current quarter
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        int currentQuarter = (currentMonth - 1) / 3 + 1;
        
        // Generate data for last 4 quarters
        for (int i = 0; i < 4; i++) {
            int year = currentYear;
            int quarter = currentQuarter - i;
            
            // Adjust year if quarter goes negative
            while (quarter <= 0) {
                quarter += 4;
                year--;
            }
            
            // Calculate quarter start and end dates
            int startMonth = (quarter - 1) * 3 + 1;
            int endMonth = startMonth + 2;
            
            LocalDateTime quarterStart = LocalDate.of(year, startMonth, 1).atStartOfDay();
            LocalDateTime quarterEnd = LocalDate.of(year, endMonth, 1)
                .plusMonths(1).minusDays(1).atTime(23, 59, 59);
            
            // If it's the current quarter, use current date as end
            if (i == 0) {
                quarterEnd = LocalDateTime.now();
            }
            
            // Get orders for this quarter
            List<Order> quarterOrders = orderRepo.findByStatusAndPaidAtBetween(
                OrderStatus.PAID, quarterStart, quarterEnd);
            
            BigDecimal quarterRevenue = quarterOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            comparisons.add(new AdminStatsResponse.QuarterlyComparison(
                "Q" + quarter + " " + year,
                quarterRevenue,
                quarterOrders.size(),
                year,
                quarter
            ));
        }
        
        // Reverse to show oldest first
        Collections.reverse(comparisons);
        return comparisons;
    }
}