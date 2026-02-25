package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(String username, String password);
    void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException;
}
