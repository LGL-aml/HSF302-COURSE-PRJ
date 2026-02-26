package com.jungle.courseshop.dto.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RAG Chat Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagChatResponse {

    private boolean success;
    private String message;
    private String response;
    private Long sessionId;
    private String intent;

    public static RagChatResponse success(Long sessionId, String response, String intent) {
        return RagChatResponse.builder()
                .success(true)
                .sessionId(sessionId)
                .response(response)
                .intent(intent)
                .build();
    }

    public static RagChatResponse error(String message) {
        return RagChatResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
