package com.jungle.courseshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.courseshop.dto.request.AiChatRequest;
import com.jungle.courseshop.dto.request.AiQuizGenerateRequest;
import com.jungle.courseshop.dto.response.AiChatResponse;
import com.jungle.courseshop.dto.response.AiQuizResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CerebrasAiService {

    private final WebClient cerebrasWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.cerebras.model:llama3.1-70b}")
    private String model;

    public AiChatResponse chat(AiChatRequest request) {
        String mode = request.getMode() == null ? "explain" : request.getMode().trim();
        String userMessage = safeTrim(request.getMessage());
        if (userMessage.isBlank()) {
            return new AiChatResponse("Vui lòng nhập câu hỏi.");
        }

        String context = safeTrim(request.getContext());
        String system = buildChatSystemPrompt(mode);
        String userPrompt = context.isBlank()
                ? userMessage
                : ("Ngữ cảnh bài học/khóa học:\n" + context + "\n\nCâu hỏi:\n" + userMessage);

        String content = callChatCompletions(system, userPrompt);
        return new AiChatResponse(content);
    }

    public AiQuizResponse generateQuiz(AiQuizGenerateRequest request) {
        String lessonText = safeTrim(request.getLessonText());
        int n = request.getNumQuestions() == null ? 5 : Math.max(1, Math.min(20, request.getNumQuestions()));
        if (lessonText.isBlank()) {
            return new AiQuizResponse(List.of());
        }

        String system = "Bạn là một trợ lý tạo câu hỏi trắc nghiệm cho bài học. " +
                "Chỉ trả về JSON hợp lệ theo đúng schema được yêu cầu, không thêm chữ giải thích.";

        String userPrompt = "Từ nội dung bài học sau, hãy tạo " + n + " câu hỏi trắc nghiệm (4 đáp án).\n" +
                "YÊU CẦU OUTPUT (JSON): {\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"question\": string,\n" +
                "      \"options\": [string,string,string,string],\n" +
                "      \"correctIndex\": 0-3,\n" +
                "      \"explanation\": string\n" +
                "    }\n" +
                "  ]\n" +
                "}\n\n" +
                "Nội dung bài học:\n" + lessonText;

        String content = callChatCompletions(system, userPrompt);
        return parseQuizJson(content);
    }

    private String buildChatSystemPrompt(String mode) {
        return switch (mode) {
            case "summarize" -> "Bạn là GIẢNG VIÊN dạy lập trình. Hãy tóm tắt ngắn gọn, rõ ràng, có gạch đầu dòng nếu cần. Nếu thiếu ngữ cảnh thì hỏi lại người học.";
            case "next_step" -> "Bạn là GIẢNG VIÊN dạy lập trình. Hãy đề xuất bước học tiếp theo cụ thể, kèm lý do ngắn và bài tập nhỏ. Nếu thiếu ngữ cảnh thì hỏi lại.";
            case "explain" -> "Bạn là GIẢNG VIÊN dạy lập trình. Hãy giải thích dễ hiểu, có ví dụ code ngắn khi phù hợp, tránh lan man. Nếu thiếu ngữ cảnh thì hỏi lại.";
            default -> "Bạn là GIẢNG VIÊN dạy lập trình. Trả lời rõ ràng, đúng trọng tâm. Nếu thiếu ngữ cảnh thì hỏi lại.";
        };
    }

    private String callChatCompletions(String systemPrompt, String userPrompt) {
        try {
            String payload = "{\n" +
                    "  \"model\": \"" + escapeJson(model) + "\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\"},\n" +
                    "    {\"role\":\"user\",\"content\":\"" + escapeJson(userPrompt) + "\"}\n" +
                    "  ],\n" +
                    "  \"temperature\": 0.4\n" +
                    "}";

            String raw = cerebrasWebClient
                    .post()
                    .uri("/chat/completions")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(45))
                    .block();

            if (raw == null || raw.isBlank()) return "";

            JsonNode node = objectMapper.readTree(raw);
            JsonNode choices = node.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode msg = choices.get(0).get("message");
                if (msg != null && msg.get("content") != null) {
                    return msg.get("content").asText("");
                }
            }
            return "";
        } catch (Exception e) {
            return "AI đang bận hoặc cấu hình chưa đúng. Chi tiết: " + e.getMessage();
        }
    }

    private AiQuizResponse parseQuizJson(String content) {
        try {
            String json = extractJsonObject(content);
            JsonNode node = objectMapper.readTree(json);
            JsonNode qs = node.get("questions");
            if (qs == null || !qs.isArray()) return new AiQuizResponse(List.of());

            List<AiQuizResponse.Question> out = new ArrayList<>();
            for (JsonNode q : qs) {
                String question = q.path("question").asText("");
                List<String> options = new ArrayList<>();
                JsonNode opts = q.get("options");
                if (opts != null && opts.isArray()) {
                    for (JsonNode o : opts) options.add(o.asText(""));
                }
                Integer correctIndex = q.has("correctIndex") ? q.get("correctIndex").asInt() : null;
                String explanation = q.path("explanation").asText("");
                out.add(new AiQuizResponse.Question(question, options, correctIndex, explanation));
            }
            return new AiQuizResponse(out);
        } catch (Exception e) {
            // fallback: không parse được -> trả rỗng
            return new AiQuizResponse(List.of());
        }
    }

    private String extractJsonObject(String s) {
        if (s == null) return "{}";
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start >= 0 && end > start) return s.substring(start, end + 1);
        return s.trim();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }
}
