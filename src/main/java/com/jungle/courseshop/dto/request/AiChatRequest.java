package com.jungle.courseshop.dto.request;

import lombok.Data;

@Data
public class AiChatRequest {
    private String message;
    private String mode; // explain | summarize | next_step

    private Long courseId;
    private Long videoId;

    // optional: nội dung bài học/transcript dán vào
    private String context;
}
