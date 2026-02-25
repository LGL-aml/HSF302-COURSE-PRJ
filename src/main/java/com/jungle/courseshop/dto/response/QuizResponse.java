package com.jungle.courseshop.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseTitle;
    private Long moduleId;
    private String moduleTitle;
    private Integer duration;
    private Integer passingScore;
    private Integer maxAttempts;
    private Integer questionCount;
    private Boolean active;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    
    // User's progress info
    private Integer userAttemptCount;
    private Double userBestScore;
    private Boolean userPassed;
}
