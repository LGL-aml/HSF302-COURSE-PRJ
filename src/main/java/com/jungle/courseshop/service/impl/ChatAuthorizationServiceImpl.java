package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.CourseEnrollmentRepo;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.service.ChatAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatAuthorizationServiceImpl implements ChatAuthorizationService {

    private final CourseRepo courseRepo;
    private final CourseEnrollmentRepo enrollmentRepo;

    @Override
    public boolean canChat(User sender, User recipient, Course course) {
        if (sender == null || recipient == null || course == null) return false;
        if (sender.getId() != recipient.getId()) {
            // Rule A: sender must be either course creator or enrolled student
            boolean isSenderCreator = course.getCreator() != null && course.getCreator().getId() == sender.getId();
            boolean isSenderEnrolled = enrollmentRepo.existsByUserAndCourse(sender, course);
            if (!isSenderCreator && !isSenderEnrolled) return false;

            // recipient must be the other side (creator or enrolled student)
            boolean isRecipientCreator = course.getCreator() != null && course.getCreator().getId() == recipient.getId();
            boolean isRecipientEnrolled = enrollmentRepo.existsByUserAndCourse(recipient, course);
            if (!isRecipientCreator && !isRecipientEnrolled) return false;
        }
        return true;
    }
}
