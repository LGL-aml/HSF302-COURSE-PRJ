package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.CourseModule;
import com.jungle.courseshop.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepo extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByCourseModule(CourseModule courseModule);
    Optional<Quiz> findByCourseModuleId(Long moduleId);
    Optional<Quiz> findByIdAndActiveTrue(Long id);
    void deleteByCourseModuleId(Long moduleId);
}
