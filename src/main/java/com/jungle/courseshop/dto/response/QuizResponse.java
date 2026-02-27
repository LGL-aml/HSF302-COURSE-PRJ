package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Integer passScore;
    private Long moduleId;
    private String moduleName;
    private List<QuestionResponse> questions;
    private Boolean active;
    private Boolean passed; // true nếu user hiện tại đã pass quiz này

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long id;
        private String questionText;
        private Integer orderIndex;
        private List<OptionResponse> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {
        private Long id;
        private String optionText;
        private Boolean isCorrect; // chỉ trả về cho lecturer hoặc sau khi nộp bài
        private String explanation; // chỉ trả về sau khi nộp bài
    }
}
