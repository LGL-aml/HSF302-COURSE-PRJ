package com.jungle.courseshop.dto.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for RAG Chat Response - aligned with frontend expectations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagChatResponse {

    private boolean success;
    private String message;       // Error message (when success=false)
    private String response;      // Raw response (backward compat)
    private String answer;        // AI-generated answer (HTML)
    private String userMessage;   // Echo of user's question
    private Long sessionId;
    private String intent;
    private double confidence;
    private List<String> retrievedContext; // Context snippets used
    private String timestamp;

    public static RagChatResponse success(Long sessionId, String answer, String intent,
                                          String userMessage, double confidence,
                                          List<String> retrievedContext) {
        return RagChatResponse.builder()
                .success(true)
                .sessionId(sessionId)
                .answer(answer)
                .response(answer)
                .userMessage(userMessage)
                .intent(intent)
                .confidence(confidence)
                .retrievedContext(retrievedContext)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static RagChatResponse error(String message) {
        return RagChatResponse.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
