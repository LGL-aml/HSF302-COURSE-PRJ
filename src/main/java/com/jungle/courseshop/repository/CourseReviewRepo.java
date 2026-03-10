package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseReviewRepo extends JpaRepository<CourseReview, Long> {

    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    Optional<CourseReview> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course.id = :courseId")
    Double findAverageRatingByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(r) FROM CourseReview r WHERE r.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT r.rating, COUNT(r) FROM CourseReview r WHERE r.course.id = :courseId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> findRatingDistributionByCourseId(@Param("courseId") Long courseId);

    // Top rated courses (for admin dashboard "Khóa học yêu thích nhất")
    @Query("SELECT r.course.id, r.course.title, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
           "FROM CourseReview r " +
           "GROUP BY r.course.id, r.course.title " +
           "HAVING COUNT(r) >= 1 " +
           "ORDER BY avgRating DESC, reviewCount DESC")
    List<Object[]> findTopRatedCourses();
}
