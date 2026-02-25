package com.jungle.courseshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LecturerRegistrationRequest {
    // Thông tin từ CCCD
    private String identifyNumber;
    private String fullName;
    private String birthDate;
    private String gender;
    private String recentLocation;
    private String nationality;


    // Thông tin bổ sung từ form
    private String phone;
    private String email;
    private String bio;
    private String domainExpertise;
}
