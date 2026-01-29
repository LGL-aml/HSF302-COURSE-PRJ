package com.jungle.courseshop.config;

import com.jungle.courseshop.service.UserDetailServiceCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailServiceCustomizer userDetailsService;

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
            "/payments/vn-pay-callback"
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
                // Các API gọi bằng fetch() (POST) nên cần bỏ qua CSRF cho /api/ai/** và /api/feedback/**
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/ai/**", "/api/feedback/**"))

                // Phân quyền URL
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        
                        // Lecturer endpoints - yêu cầu role LECTURER
                        .requestMatchers("/courses/lecture/**")
                        .hasAuthority("LECTURER")

                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        .requestMatchers("/instructor/**")
                        .hasRole("INSTRUCTOR")

                        .requestMatchers("/student/**")
                        .hasRole("STUDENT")

                        .anyRequest().authenticated()
                )

                // Login form
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
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
