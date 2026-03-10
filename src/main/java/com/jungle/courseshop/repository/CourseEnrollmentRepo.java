package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.CourseEnrollment;
import com.jungle.courseshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepo extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByUserOrderByEnrollmentDateDesc(User user);
    List<CourseEnrollment> findByCourse(Course course);
    Optional<CourseEnrollment> findByUserAndCourse(User user, Course course);
    boolean existsByUserAndCourse(User user, Course course);
    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    long countByCourse(Course course);

    long countByEnrollmentDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course.topic.id = :topicId")
    long countByTopicId(@Param("topicId") Long topicId);
}
