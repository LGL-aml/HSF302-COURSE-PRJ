package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.request.CourseCreateRequest;
import com.jungle.courseshop.dto.request.CourseUpdateRequest;
import com.jungle.courseshop.dto.response.CourseDetailPublicResponse;
import com.jungle.courseshop.dto.response.CourseHomeResponse;
import com.jungle.courseshop.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CourseService {

    CourseResponse createCourse(CourseCreateRequest request) throws IOException;

    CourseResponse updateCourse(Long id, CourseUpdateRequest request) throws IOException;

    List<CourseResponse> getCreatedCourses();

    void softDeleteCourse(Long id);

    Page<CourseHomeResponse> searchCoursesSummary(String keyword, Long topicId, Pageable pageable);

    List<CourseHomeResponse> getLastestCourses();

    CourseDetailPublicResponse getCoursePublicDetail(Long id);

    CourseResponse getCourseById(Long id);

    List<CourseResponse> getCoursesByLecturer();

    void deleteCourse(Long courseId);
}

