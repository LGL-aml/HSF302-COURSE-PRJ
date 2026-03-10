package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.response.AdminStatsResponse;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStatsService {

    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final TopicRepo topicRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CourseEnrollmentRepo enrollmentRepo;
    private final LecturerRepo lecturerRepo;

    public AdminStatsResponse getDashboardStats(String period) {
        return getDashboardStats(period, null, null);
    }

    public AdminStatsResponse getDashboardStats(String period, LocalDate customStartDate, LocalDate customEndDate) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();
        String periodType = period != null ? period : "month";

        if ("custom".equals(periodType) && customStartDate != null && customEndDate != null) {
            startDate = customStartDate.atStartOfDay();
            endDate = customEndDate.atTime(23, 59, 59);
        } else if ("year".equals(periodType)) {
            startDate = endDate.minusMonths(12).withDayOfMonth(1).toLocalDate().atStartOfDay();
        } else if ("quarter".equals(periodType)) {
            startDate = endDate.minusMonths(12).toLocalDate().atStartOfDay();
        } else {
            periodType = "month";
            startDate = endDate.minusDays(30).toLocalDate().atStartOfDay();
        }

        // ============ Tổng quan ============
        long totalUsers = userRepo.countByEnabledTrue();
        long totalCourses = courseRepo.count();
        long totalTopics = topicRepo.count();
        long totalLecturers = lecturerRepo.count();
        long totalEnrollments = enrollmentRepo.count();

        // Đơn hàng đã thanh toán trong khoảng thời gian
        List<Order> paidOrders = orderRepo.findByStatusAndPaidAtBetween(OrderStatus.PAID, startDate, endDate);

        BigDecimal totalRevenue = paidOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenuePerOrder = paidOrders.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(paidOrders.size()), 0, RoundingMode.HALF_UP);

        // User & enrollment mới trong khoảng TG
        long newEnrollmentsThisPeriod = enrollmentRepo.countByEnrollmentDateBetween(startDate, endDate);

        // ============ Top khóa học đăng ký nhiều nhất ============
        List<AdminStatsResponse.TopCourseStat> topCourses = courseRepo.findTop5ByActiveTrueOrderByEnrolledCountDesc()
                .stream()
                .map(c -> new AdminStatsResponse.TopCourseStat(c.getId(), c.getTitle(), c.getEnrolledCount()))
                .collect(Collectors.toList());

        // ============ Khóa học bán chạy nhất trong period ============
        List<AdminStatsResponse.BestSellingCourse> bestSellingCourses = getBestSellingCourses(startDate, endDate, 10);

        // ============ Phân bố theo chủ đề ============
        List<AdminStatsResponse.TopicDistribution> topicDistributions = getTopicDistributions();

        // ============ Chart data ============
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenueSeries = new ArrayList<>();
        List<Long> orderSeries = new ArrayList<>();
        List<Long> enrollmentSeries = new ArrayList<>();
        List<AdminStatsResponse.QuarterlyComparison> quarterlyComparisons = new ArrayList<>();
        List<AdminStatsResponse.QuarterlyBestSeller> quarterlyBestSellers = new ArrayList<>();

        if ("quarter".equals(periodType)) {
            quarterlyComparisons = generateQuarterlyData();
            quarterlyBestSellers = generateQuarterlyBestSellers();

            LocalDate current = LocalDate.now().withDayOfMonth(1);
            for (int i = 11; i >= 0; i--) {
                YearMonth month = YearMonth.from(current.minusMonths(i));
                labels.add(month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                LocalDateTime ms = month.atDay(1).atStartOfDay();
                LocalDateTime me = month.atEndOfMonth().atTime(23, 59, 59);
                addPeriodData(ms, me, revenueSeries, orderSeries, enrollmentSeries);
            }
        } else if ("year".equals(periodType)) {
            LocalDate current = LocalDate.now().withDayOfMonth(1);
            for (int i = 11; i >= 0; i--) {
                YearMonth month = YearMonth.from(current.minusMonths(i));
                labels.add(month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                LocalDateTime ms = month.atDay(1).atStartOfDay();
                LocalDateTime me = month.atEndOfMonth().atTime(23, 59, 59);
                addPeriodData(ms, me, revenueSeries, orderSeries, enrollmentSeries);
            }
        } else if ("custom".equals(periodType) && customStartDate != null && customEndDate != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(customStartDate, customEndDate);
            if (daysBetween <= 31) {
                for (LocalDate date = customStartDate; !date.isAfter(customEndDate); date = date.plusDays(1)) {
                    labels.add(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    LocalDateTime ds = date.atStartOfDay();
                    LocalDateTime de = date.atTime(23, 59, 59);
                    addPeriodData(ds, de, revenueSeries, orderSeries, enrollmentSeries);
                }
            } else {
                YearMonth startMonth = YearMonth.from(customStartDate);
                YearMonth endMonth = YearMonth.from(customEndDate);
                for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
                    labels.add(month.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                    LocalDateTime ms = month.equals(startMonth) ? customStartDate.atStartOfDay() : month.atDay(1).atStartOfDay();
                    LocalDateTime me = month.equals(endMonth) ? customEndDate.atTime(23, 59, 59) : month.atEndOfMonth().atTime(23, 59, 59);
                    addPeriodData(ms, me, revenueSeries, orderSeries, enrollmentSeries);
                }
            }
        } else {
            // Default: 30 ngày
            LocalDate current = LocalDate.now();
            for (int i = 29; i >= 0; i--) {
                LocalDate day = current.minusDays(i);
                labels.add(day.format(DateTimeFormatter.ofPattern("dd/MM")));
                LocalDateTime ds = day.atStartOfDay();
                LocalDateTime de = day.atTime(23, 59, 59);
                addPeriodData(ds, de, revenueSeries, orderSeries, enrollmentSeries);
            }
        }

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalCourses(totalCourses)
                .totalTopics(totalTopics)
                .totalPaidOrders(paidOrders.size())
                .totalRevenue(totalRevenue)
                .totalLecturers(totalLecturers)
                .totalEnrollments(totalEnrollments)
                .newUsersThisPeriod(0) // can be computed if needed
                .newEnrollmentsThisPeriod(newEnrollmentsThisPeriod)
                .avgRevenuePerOrder(avgRevenuePerOrder)
                .labels(labels)
                .revenueSeries(revenueSeries)
                .paidOrdersSeries(orderSeries)
                .enrollmentSeries(enrollmentSeries)
                .topCourses(topCourses)
                .bestSellingCourses(bestSellingCourses)
                .topicDistributions(topicDistributions)
                .quarterlyComparisons(quarterlyComparisons)
                .quarterlyBestSellers(quarterlyBestSellers)
                .periodType(periodType)
                .startDate(customStartDate != null ? customStartDate : startDate.toLocalDate())
                .endDate(customEndDate != null ? customEndDate : endDate.toLocalDate())
                .build();
    }

    private void addPeriodData(LocalDateTime start, LocalDateTime end,
                               List<BigDecimal> revenueSeries,
                               List<Long> orderSeries,
                               List<Long> enrollmentSeries) {
        List<Order> orders = orderRepo.findByStatusAndPaidAtBetween(OrderStatus.PAID, start, end);
        BigDecimal revenue = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        revenueSeries.add(revenue);
        orderSeries.add((long) orders.size());
        enrollmentSeries.add(enrollmentRepo.countByEnrollmentDateBetween(start, end));
    }

    private List<AdminStatsResponse.BestSellingCourse> getBestSellingCourses(LocalDateTime start, LocalDateTime end, int limit) {
        try {
            List<Object[]> results = orderItemRepo.findBestSellingCourses(OrderStatus.PAID, start, end);
            return results.stream()
                    .limit(limit)
                    .map(row -> new AdminStatsResponse.BestSellingCourse(
                            (Long) row[0],
                            (String) row[1],
                            (String) row[2],
                            (Long) row[3],
                            (BigDecimal) row[4],
                            (BigDecimal) row[5]
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error fetching best selling courses: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<AdminStatsResponse.TopicDistribution> getTopicDistributions() {
        try {
            return topicRepo.findAll().stream()
                    .map(topic -> {
                        long courseCount = courseRepo.findByActiveTrue().stream()
                                .filter(c -> c.getTopic() != null && c.getTopic().getId().equals(topic.getId()))
                                .count();
                        long enrollCount = 0;
                        try {
                            enrollCount = enrollmentRepo.countByTopicId(topic.getId());
                        } catch (Exception ignored) {}
                        return new AdminStatsResponse.TopicDistribution(
                                topic.getName(), courseCount, enrollCount
                        );
                    })
                    .filter(td -> td.getCourseCount() > 0)
                    .sorted((a, b) -> Long.compare(b.getCourseCount(), a.getCourseCount()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error fetching topic distributions: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<AdminStatsResponse.QuarterlyComparison> generateQuarterlyData() {
        List<AdminStatsResponse.QuarterlyComparison> comparisons = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;

        for (int i = 0; i < 4; i++) {
            int year = currentYear;
            int quarter = currentQuarter - i;
            while (quarter <= 0) { quarter += 4; year--; }

            int startMonth = (quarter - 1) * 3 + 1;
            LocalDateTime quarterStart = LocalDate.of(year, startMonth, 1).atStartOfDay();
            LocalDateTime quarterEnd = (i == 0) ? LocalDateTime.now() :
                    LocalDate.of(year, startMonth, 1).plusMonths(3).minusDays(1).atTime(23, 59, 59);

            List<Order> qOrders = orderRepo.findByStatusAndPaidAtBetween(OrderStatus.PAID, quarterStart, quarterEnd);
            BigDecimal qRevenue = qOrders.stream()
                    .map(Order::getTotalAmount).filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            comparisons.add(new AdminStatsResponse.QuarterlyComparison(
                    "Q" + quarter + " " + year, qRevenue, qOrders.size(), year, quarter));
        }
        Collections.reverse(comparisons);
        return comparisons;
    }

    private List<AdminStatsResponse.QuarterlyBestSeller> generateQuarterlyBestSellers() {
        List<AdminStatsResponse.QuarterlyBestSeller> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;

        for (int i = 0; i < 4; i++) {
            int year = currentYear;
            int quarter = currentQuarter - i;
            while (quarter <= 0) { quarter += 4; year--; }

            int startMonth = (quarter - 1) * 3 + 1;
            LocalDateTime quarterStart = LocalDate.of(year, startMonth, 1).atStartOfDay();
            LocalDateTime quarterEnd = (i == 0) ? LocalDateTime.now() :
                    LocalDate.of(year, startMonth, 1).plusMonths(3).minusDays(1).atTime(23, 59, 59);

            List<AdminStatsResponse.BestSellingCourse> courses = getBestSellingCourses(quarterStart, quarterEnd, 5);
            result.add(new AdminStatsResponse.QuarterlyBestSeller(
                    "Q" + quarter + " " + year, year, quarter, courses));
        }
        Collections.reverse(result);
        return result;
    }
}