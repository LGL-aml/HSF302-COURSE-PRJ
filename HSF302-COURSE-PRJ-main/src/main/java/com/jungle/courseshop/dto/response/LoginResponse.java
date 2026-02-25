package com.jungle.courseshop.dto.response;

import com.jungle.courseshop.entity.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String avatar;
    private Role role;
    private String redirectUrl;
    private String message;
}
