package com.jungle.courseshop.dto.request;

import lombok.Data;

@Data
public class AiQuizGenerateRequest {
    private Long courseId;
    private Long videoId;

    private String lessonText;
    private Integer numQuestions;
}
