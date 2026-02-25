package com.jungle.courseshop.dto.response;

import com.jungle.courseshop.entity.LecturerStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LecturerResponse {
    Long id;
    Long userId;
    String username;
    String fullname;
    String email;
    String avatar;
    String bio;
    String address;
    String gender;
    String domainExpertise;
    String identityNumber;
    LecturerStatus status;
    LocalDateTime registeredAt;
    Double averageRating;
    Integer totalStudents;
}

