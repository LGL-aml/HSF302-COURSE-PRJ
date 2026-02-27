package com.jungle.courseshop.dto.request;

import lombok.Data;

import java.util.Map;

/**
 * DTO cho việc nộp bài quiz
 * Key: questionId, Value: optionId đã chọn
 */
@Data
public class QuizSubmitRequest {
    private Long quizId;
    private Map<Long, Long> answers; // questionId -> selectedOptionId
}
