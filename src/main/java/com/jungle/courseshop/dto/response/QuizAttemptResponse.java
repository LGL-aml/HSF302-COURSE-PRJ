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
public class QuizAttemptResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Integer attemptNumber;
    private Double score;
    private Integer totalPoints;
    private Double percentage;
    private Boolean passed;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startedAt;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime submittedAt;
    
    private Integer timeSpent; // seconds
    
    // Detailed answers (only after submission)
    private List<AnswerResult> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResult {
        private Long questionId;
        private String question;
        private String questionType;
        private Integer points;
        private String selectedAnswerIds;
        private String textAnswer;
        private Boolean isCorrect;
        private Integer pointsEarned;
        private String correctAnswerIds;
        private String explanation;
        private List<AnswerOption> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerOption {
        private Long id;
        private String text;
        private Boolean isCorrect;
        private Boolean wasSelected;
    }
}
