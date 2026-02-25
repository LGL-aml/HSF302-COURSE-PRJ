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
public class QuizDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private Integer passingScore;
    private Integer maxAttempts;
    private Integer totalPoints;
    private List<QuestionDetail> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDetail {
        private Long id;
        private String question;
        private String type;
        private Integer points;
        private List<AnswerDetail> answers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetail {
        private Long id;
        private String answerText;
        // Don't send isCorrect to client before submission
    }
}
