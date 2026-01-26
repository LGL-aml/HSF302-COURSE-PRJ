package com.jungle.courseshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
    private String title;
    private String content;
    private String type;
    private LocalDateTime createdAt;
}
