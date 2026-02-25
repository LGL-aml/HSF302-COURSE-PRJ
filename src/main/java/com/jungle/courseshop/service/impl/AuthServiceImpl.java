package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.response.LoginResponse;
import com.jungle.courseshop.entity.Role;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;

    @Override
    public LoginResponse login(String username, String password) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy thông tin user sau khi đăng nhập thành công
        User user = userRepo.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Xác định URL redirect dựa trên role
        String redirectUrl = determineRedirectUrl(user.getRole());
        log.info("Redirecting to " + redirectUrl);

        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .redirectUrl(redirectUrl)
                .message("Đăng nhập thành công")
                .build();
    }

    private String determineRedirectUrl(Role role) {
        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case LECTURER -> "/lecturer/courses";
            default -> "/";
        };
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.logout();
    }
}
