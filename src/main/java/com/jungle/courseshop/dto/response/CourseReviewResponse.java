package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseReviewResponse {
    private Long id;
    private Long userId;
    private String username;
    private String fullname;
    private String avatar;
    private Long courseId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
