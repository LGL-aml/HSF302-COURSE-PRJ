package com.jungle.courseshop.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {

    void sendWelcomeEmail(String toEmail, String fullName) throws MessagingException, UnsupportedEncodingException;

    void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException;

    void sendEnrollmentSuccessEmail(String toEmail, String fullName, String courseTitle,
                                     int courseDuration, String instructorName,
                                     String enrollmentDate) throws MessagingException, UnsupportedEncodingException;

    void sendCourseCompletionEmail(String toEmail, String fullName, String courseTitle,
                                    int courseDuration, String instructorName,
                                    String completionDate) throws MessagingException, UnsupportedEncodingException;
}
