package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.request.LecturerRegistrationRequest;
import com.jungle.courseshop.dto.request.RegisterRequest;
import com.jungle.courseshop.dto.request.UpdateUserRequest;
import com.jungle.courseshop.dto.response.RegisterResponse;
import com.jungle.courseshop.dto.response.UpdateUserResponse;
import com.jungle.courseshop.dto.response.UserDetailResponse;
import com.jungle.courseshop.entity.Lecturer;
import com.jungle.courseshop.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserService {

    RegisterResponse createUser(RegisterRequest request);

    List<UserDetailResponse> getAllUsers();

    UserDetailResponse getUsersById(Long userId);

    UserDetailResponse getUserByUsername();

    UpdateUserResponse updateUserProfile(UpdateUserRequest request) throws IOException;

    UpdateUserResponse updateUser(Long userId, UpdateUserRequest request);

    void deleteUser(Long userId);

    Lecturer registerLecturer(User user, LecturerRegistrationRequest request);

    void approveLecturer(Long lecturerId);

    void rejectLecturer(Long lecturerId, String reason);

    List<Lecturer> getLecturers();

    Lecturer getLecturerById(Long lecturerId);

}
