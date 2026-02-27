package com.jungle.courseshop.dto.rag;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RAG Chat Request - supports both 'message' and 'userMessage' field names
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagChatRequest {

    @NotBlank(message = "Message cannot be blank")
    @JsonAlias({"userMessage", "question"})
    private String message;

    private Long sessionId;

    private String userId;
}
