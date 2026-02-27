package com.jungle.courseshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO cho quiz khi gửi từ form tạo/sửa khóa học
 * Được nhúng trong JSON modules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {
    private String title;
    private String description;
    private Integer passScore = 70;
    private List<QuestionRequest> questions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionRequest {
        private String questionText;
        private Integer orderIndex;
        private List<OptionRequest> options;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionRequest {
        private String optionText;
        private Boolean correct = false;
        private String explanation;
    }
}
