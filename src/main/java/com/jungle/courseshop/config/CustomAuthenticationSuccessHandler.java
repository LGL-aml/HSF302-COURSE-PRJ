package com.jungle.courseshop.config;

import com.jungle.courseshop.entity.Role;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        User user = userRepo.findByUsernameAndEnabledTrue(username).orElse(null);

        String redirectUrl = "/";

        if (user != null) {
            Role role = user.getRole();
            redirectUrl = determineRedirectUrl(role);
            log.info("User {} logged in with role: {}, redirecting to: {}", username, role, redirectUrl);
        }

        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(Role role) {
        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case LECTURER -> "/lecturer/courses";
            default -> "/";
        };
    }
}

