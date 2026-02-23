package com.jungle.courseshop.service;

import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.User;

public interface ChatAuthorizationService {
    boolean canChat(User sender, User recipient, Course course);
}
