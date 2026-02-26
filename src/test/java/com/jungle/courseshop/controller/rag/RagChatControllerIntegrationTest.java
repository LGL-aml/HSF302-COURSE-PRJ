package com.jungle.courseshop.controller.rag;

import com.jungle.courseshop.dto.rag.RagChatRequest;
import com.jungle.courseshop.dto.rag.RagChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test for RAG Chat Controller
 * 
 * NOTE: Requires:
 * 1. MySQL database running
 * 2. GEMINI_API_KEY configured in .env
 * 3. Active courses in database for embeddings
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RagChatControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCourseSearch() {
        // Given
        RagChatRequest request = RagChatRequest.builder()
                .message("Tìm khóa học Java")
                .userId("test-user")
                .build();

        // When
        ResponseEntity<RagChatResponse> response = restTemplate.postForEntity(
                "/api/rag/chat",
                request,
                RagChatResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getResponse());
        assertNotNull(response.getBody().getSessionId());
        System.out.println("Intent: " + response.getBody().getIntent());
        System.out.println("Response: " + response.getBody().getResponse());
    }

    @Test
    void testCourseRecommend() {
        // Given
        RagChatRequest request = RagChatRequest.builder()
                .message("Tôi muốn học lập trình web, nên bắt đầu từ đâu?")
                .userId("test-user")
                .build();

        // When
        ResponseEntity<RagChatResponse> response = restTemplate.postForEntity(
                "/api/rag/chat",
                request,
                RagChatResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        System.out.println("Intent: " + response.getBody().getIntent());
        System.out.println("Response: " + response.getBody().getResponse());
    }

    @Test
    void testPricingInfo() {
        // Given
        RagChatRequest request = RagChatRequest.builder()
                .message("Có khóa học nào dưới 500k không?")
                .userId("test-user")
                .build();

        // When
        ResponseEntity<RagChatResponse> response = restTemplate.postForEntity(
                "/api/rag/chat",
                request,
                RagChatResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        System.out.println("Intent: " + response.getBody().getIntent());
        System.out.println("Response: " + response.getBody().getResponse());
    }

    @Test
    void testDiscountPolicy() {
        // Given
        RagChatRequest request = RagChatRequest.builder()
                .message("Có giảm giá cho sinh viên không?")
                .userId("test-user")
                .build();

        // When
        ResponseEntity<RagChatResponse> response = restTemplate.postForEntity(
                "/api/rag/chat",
                request,
                RagChatResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        System.out.println("Intent: " + response.getBody().getIntent());
        System.out.println("Response: " + response.getBody().getResponse());
    }

    @Test
    void testConversationHistory() {
        // Given - First message
        RagChatRequest request1 = RagChatRequest.builder()
                .message("Tìm khóa học Spring Boot")
                .userId("test-user")
                .build();

        // When - Send first message
        ResponseEntity<RagChatResponse> response1 = restTemplate.postForEntity(
                "/api/rag/chat",
                request1,
                RagChatResponse.class
        );

        // Then - Get session ID
        assertNotNull(response1.getBody());
        Long sessionId = response1.getBody().getSessionId();
        assertNotNull(sessionId);

        // When - Send second message with same session
        RagChatRequest request2 = RagChatRequest.builder()
                .message("Khóa học đó giá bao nhiêu?")
                .sessionId(sessionId)
                .userId("test-user")
                .build();

        ResponseEntity<RagChatResponse> response2 = restTemplate.postForEntity(
                "/api/rag/chat",
                request2,
                RagChatResponse.class
        );

        // Then
        assertNotNull(response2.getBody());
        assertEquals(sessionId, response2.getBody().getSessionId());
        
        System.out.println("=== Conversation ===");
        System.out.println("User: " + request1.getMessage());
        System.out.println("AI: " + response1.getBody().getResponse());
        System.out.println("\nUser: " + request2.getMessage());
        System.out.println("AI: " + response2.getBody().getResponse());
    }
}
