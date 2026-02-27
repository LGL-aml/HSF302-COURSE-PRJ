package com.jungle.courseshop.config;

import com.jungle.courseshop.service.impl.UserDetailServiceCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailServiceCustomizer userDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    private final String[] PUBLIC_URLS = {
            "/",
            "/login",
            "/register",
            "/auth/**",
            "/courses",
            "/courses/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/static/**",
            "/lecturer/kyc/**",
            "/payments/vn-pay-callback",
            "/ws/**"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);


        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF (MVC nên BẬT)
                // Các API gọi bằng fetch() (POST) nên cần bỏ qua CSRF cho /api/ai/**, /api/feedback/**, /api/rag/**
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/ai/**", "/api/feedback/**", "/api/rag/**", "/api/quiz/**", "/api/videos/**"))

                // Phân quyền URL
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        
                        // Lecturer endpoints - yêu cầu role LECTURER
                        .requestMatchers("/courses/lecture/**")
                        .hasAuthority("LECTURER")

                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        .requestMatchers("/lecturer/**").hasAuthority("LECTURER")

                        .requestMatchers("/instructor/**")
                        .hasRole("INSTRUCTOR")

                        .requestMatchers("/student/**")
                        .hasRole("STUDENT")

                        .anyRequest().authenticated()
                )

                // Login form - sử dụng custom success handler
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
