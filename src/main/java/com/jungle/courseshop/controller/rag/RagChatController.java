package com.jungle.courseshop.controller.rag;

import com.jungle.courseshop.dto.rag.RagChatRequest;
import com.jungle.courseshop.dto.rag.RagChatResponse;
import com.jungle.courseshop.entity.rag.RagChatMessage;
import com.jungle.courseshop.service.rag.RagChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for RAG Chat API
 */
@RestController
@RequestMapping("/api/rag/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RagChatController {

    private final RagChatService ragChatService;

    /**
     * Send a message and get AI response with RAG
     */
    @PostMapping
    public ResponseEntity<RagChatResponse> chat(@Valid @RequestBody RagChatRequest request) {
        log.info("Received RAG chat request: {}", request.getMessage());
        RagChatResponse response = ragChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get conversation history
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<RagChatMessage>> getHistory(@PathVariable Long sessionId) {
        List<RagChatMessage> history = ragChatService.getHistory(sessionId);
        return ResponseEntity.ok(history);
    }
}
