package com.jungle.courseshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long recipientId;
    private String recipientName;
    private String content;
    private String sentAt;
}