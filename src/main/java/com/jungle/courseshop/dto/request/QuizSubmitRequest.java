package com.jungle.courseshop.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequest {
    private Long quizId;
    private Long attemptId;
    private List<AnswerSubmit> answers;

    @Data
    public static class AnswerSubmit {
        private Long questionId;
        private String selectedAnswerIds; // "1,2,3" for multiple select
        private String textAnswer; // For text/code questions
    }
}
