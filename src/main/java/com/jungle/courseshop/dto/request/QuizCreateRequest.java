package com.jungle.courseshop.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QuizCreateRequest {
    private String title;
    private String description;
    private Long courseId;
    private Long moduleId;
    private Integer duration; // minutes
    private Integer passingScore; // percentage
    private Integer maxAttempts;
    private List<QuestionRequest> questions;

    @Data
    public static class QuestionRequest {
        private String question;
        private String type; // MULTIPLE_CHOICE, TRUE_FALSE, etc.
        private Integer points;
        private String explanation;
        private List<AnswerRequest> answers;
    }

    @Data
    public static class AnswerRequest {
        private String answerText;
        private Boolean isCorrect;
    }
}
