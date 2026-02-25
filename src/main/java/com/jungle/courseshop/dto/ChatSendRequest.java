package com.jungle.courseshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSendRequest {
    private Long courseId;
    private Long recipientId;
    private String content;
}
