package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {

    private long totalUsers;
    private long totalCourses;
    private long totalTopics;

    private long totalPaidOrders;
    private BigDecimal totalRevenue;

    // Thống kê bổ sung
    private long totalLecturers;
    private long totalEnrollments;
    private long newUsersThisPeriod;
    private long newEnrollmentsThisPeriod;
    private BigDecimal avgRevenuePerOrder;

    private List<String> labels;
    private List<BigDecimal> revenueSeries;
    private List<Long> paidOrdersSeries;
    private List<Long> enrollmentSeries; // Biểu đồ đăng ký theo thời gian

    private List<TopCourseStat> topCourses;
    
    // Khóa học bán chạy nhất theo quý
    @Builder.Default
    private List<BestSellingCourse> bestSellingCourses = new ArrayList<>();

    // Phân bố theo chủ đề
    @Builder.Default
    private List<TopicDistribution> topicDistributions = new ArrayList<>();

    // Quarterly comparison data
    @Builder.Default
    private List<QuarterlyComparison> quarterlyComparisons = new ArrayList<>();
    
    // Khóa học bán chạy nhất theo từng quý
    @Builder.Default
    private List<QuarterlyBestSeller> quarterlyBestSellers = new ArrayList<>();

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestSellingCourse {
        private Long courseId;
        private String title;
        private String creatorName;
        private long soldCount;
        private BigDecimal totalSales;
        private BigDecimal price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicDistribution {
        private String topicName;
        private long courseCount;
        private long enrollmentCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuarterlyBestSeller {
        private String quarter;
        private int year;
        private int quarterNumber;
        private List<BestSellingCourse> courses;
    }
}
