package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalUsers;
    private long totalCourses;
    private long totalTopics;

    private long totalPaidOrders;
    private BigDecimal totalRevenue;

    private List<String> labels;
    private List<BigDecimal> revenueSeries;
    private List<Long> paidOrdersSeries;

    private List<TopCourseStat> topCourses;
    
    // Quarterly comparison data
    private List<QuarterlyComparison> quarterlyComparisons;
    
    // Metadata about the period
    private String periodType; // "month", "year", "quarter", "custom"
    private LocalDate startDate;
    private LocalDate endDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCourseStat {
        private Long courseId;
        private String title;
        private long enrollments;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuarterlyComparison {
        private String quarter; // e.g., "Q1 2026", "Q2 2025"
        private BigDecimal revenue;
        private long orders;
        private int year;
        private int quarterNumber; // 1, 2, 3, 4
    }
}
