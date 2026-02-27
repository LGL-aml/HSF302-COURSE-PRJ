package com.jungle.courseshop.service.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.*;

/**
 * Multi-LLM Service: Thử Gemini trước → nếu lỗi (429/400) → fallback Cerebras.
 * Đảm bảo hệ thống luôn có AI response.
 */
@Service
@Slf4j
public class GeminiService {

    private final WebClient geminiClient;
    private final WebClient cerebrasClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.cerebras.model:llama-3.3-70b}")
    private String cerebrasModel;

    public GeminiService(
            @Value("${spring.ai.openai.api-key:}") String cerebrasApiKey,
            @Value("${spring.ai.openai.base-url:https://api.cerebras.ai/v1}") String cerebrasBaseUrl) {

        this.geminiClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        this.cerebrasClient = WebClient.builder()
                .baseUrl(cerebrasBaseUrl)
                .defaultHeader("Authorization", "Bearer " + cerebrasApiKey)
                .defaultHeader("Content-Type", "application/json")
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();

        log.info("Multi-LLM Service: Gemini ({}) + Cerebras ({}) at {}", 
                "gemini-2.0-flash", "llama-3.3-70b", cerebrasBaseUrl);
    }

    /**
     * Main entry: try Gemini → if fail → try Cerebras → if fail → return null
     */
    public String generateResponse(String systemPrompt, String userMessage, String apiKey) {
        // 1. Try Gemini
        if (apiKey != null && !apiKey.isBlank()) {
            String result = callGemini(systemPrompt, userMessage, apiKey);
            if (result != null) return result;
        }

        // 2. Fallback to Cerebras
        log.info("Falling back to Cerebras...");
        String result = callCerebras(systemPrompt, userMessage);
        if (result != null) return result;

        log.warn("All LLM providers failed");
        return null;
    }

    // ==================== GEMINI ====================

    private String callGemini(String systemPrompt, String userMessage, String apiKey) {
        try {
            List<Map<String, Object>> contents = new ArrayList<>();

            // Inject system prompt as user→model conversation
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text",
                            "[SYSTEM]\n" + systemPrompt + "\n[/SYSTEM]\nXác nhận bạn hiểu."))
            ));
            contents.add(Map.of(
                    "role", "model",
                    "parts", List.of(Map.of("text",
                            "Đã hiểu. Tôi sẽ tuân thủ hướng dẫn trên."))
            ));
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", userMessage))
            ));

            Map<String, Object> payload = new HashMap<>();
            payload.put("contents", contents);
            payload.put("generationConfig", Map.of(
                    "temperature", 0.85,
                    "topP", 0.95,
                    "maxOutputTokens", 1500
            ));

            String jsonBody = mapper.writeValueAsString(payload);

            String raw = geminiClient.post()
                    .uri(uri -> uri.path("/models/{model}:generateContent")
                            .queryParam("key", apiKey).build(geminiModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(25))
                    .block();

            if (raw == null) return null;

            JsonNode root = mapper.readTree(raw);
            JsonNode candidates = root.get("candidates");
            if (candidates != null && candidates.isArray() && !candidates.isEmpty()) {
                String text = candidates.get(0).path("content").path("parts")
                        .get(0).path("text").asText(null);
                if (text != null && !text.isBlank()) {
                    log.info("✅ Gemini OK: {} chars", text.length());
                    return text;
                }
            }
            return null;

        } catch (WebClientResponseException e) {
            log.warn("❌ Gemini HTTP {}: {}", e.getStatusCode(),
                    truncate(e.getResponseBodyAsString(), 200));
            return null;
        } catch (Exception e) {
            log.warn("❌ Gemini error: {}", e.getMessage());
            return null;
        }
    }

    // ==================== CEREBRAS (OpenAI-compatible) ====================

    private String callCerebras(String systemPrompt, String userMessage) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", cerebrasModel);
            payload.put("temperature", 0.7);
            payload.put("max_tokens", 1500);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));
            payload.put("messages", messages);

            String jsonBody = mapper.writeValueAsString(payload);

            String raw = cerebrasClient.post()
                    .uri("/chat/completions")
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (raw == null) return null;

            JsonNode root = mapper.readTree(raw);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && !choices.isEmpty()) {
                String text = choices.get(0).path("message").path("content").asText(null);
                if (text != null && !text.isBlank()) {
                    log.info("✅ Cerebras OK: {} chars", text.length());
                    return text;
                }
            }
            return null;

        } catch (WebClientResponseException e) {
            log.warn("❌ Cerebras HTTP {}: {}", e.getStatusCode(),
                    truncate(e.getResponseBodyAsString(), 200));
            return null;
        } catch (Exception e) {
            log.warn("❌ Cerebras error: {}", e.getMessage());
            return null;
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
