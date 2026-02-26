package com.jungle.courseshop.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RAG Chat Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagChatRequest {

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private Long sessionId;

    private String userId;
}
