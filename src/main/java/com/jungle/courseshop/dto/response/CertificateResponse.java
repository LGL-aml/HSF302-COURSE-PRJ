package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateResponse {
    private Long id;
    private String courseTitle;
    private String username;
    private String lecturerName;
    private String certificateUrl;
    private String courseCoverImage;
    private LocalDateTime issuedDate;
    private Long courseId;
}
