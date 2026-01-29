package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String avatar;
    private LocalDateTime createdAt;
    private List<FeedbackResponse> replies;
}
