package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.CourseEnrollmentResponse;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.CourseEnrollment;
import com.jungle.courseshop.entity.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface CourseEnrollmentService {

    CourseEnrollmentResponse enrollCourse(Long courseId) throws MessagingException, UnsupportedEncodingException;

    CourseEnrollment enrollUserToCourse(User user, Course course) throws MessagingException, UnsupportedEncodingException;

    List<CourseEnrollmentResponse> getEnrolledCourses();

    void markVideoAsWatched(Long videoId, boolean watchedStatus) throws MessagingException, UnsupportedEncodingException;
}

