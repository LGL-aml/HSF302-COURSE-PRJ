package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCourseStat {
        private Long courseId;
        private String title;
        private long enrollments;
    }
}
