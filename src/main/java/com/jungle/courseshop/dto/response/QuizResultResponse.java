package com.jungle.courseshop.dto.response;

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
public class QuizResultResponse {
    private Long quizId;
    private String quizTitle;
    private Double score;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Boolean passed;
    private Integer passScore;
    private LocalDateTime completedAt;
    private Long attemptCount;
    private List<QuestionResult> questionResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private Long questionId;
        private String questionText;
        private Long selectedOptionId;
        private Long correctOptionId;
        private Boolean isCorrect;
        private String explanation;
        private List<OptionResult> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResult {
        private Long id;
        private String optionText;
        private Boolean isCorrect;
        private Boolean isSelected;
    }
}
