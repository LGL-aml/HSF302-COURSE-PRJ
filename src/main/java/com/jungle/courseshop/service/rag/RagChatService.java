package com.jungle.courseshop.service.rag;

import com.jungle.courseshop.dto.rag.IntentResult;
import com.jungle.courseshop.dto.rag.IntentResult.IntentType;
import com.jungle.courseshop.dto.rag.RagChatRequest;
import com.jungle.courseshop.dto.rag.RagChatResponse;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.CourseModule;
import com.jungle.courseshop.entity.Topic;
import com.jungle.courseshop.entity.rag.RagChatMessage;
import com.jungle.courseshop.entity.rag.RagChatMessage.MessageRole;
import com.jungle.courseshop.entity.rag.RagChatSession;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.TopicRepo;
import com.jungle.courseshop.repository.rag.RagChatMessageRepository;
import com.jungle.courseshop.repository.rag.RagChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hybrid RAG Chat Service
 * 
 * MÔ HÌNH HYBRID:
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Câu hỏi về khóa học / giá / tư vấn                       │
 * │  → FULL RAG: Truy xuất MySQL metadata → Gemini trả lời    │
 * │     dựa trên data thực + hiển thị course cards             │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Câu hỏi về lộ trình / kiến thức chung / tech advice      │
 * │  → HYBRID: Gemini trả lời tự do bằng tri thức riêng       │
 * │     + bổ sung data từ DB nếu có liên quan                  │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Chào hỏi / trò chuyện                                    │
 * │  → FREE: Gemini trả lời tự nhiên, kèm giới thiệu hệ thống│
 * └─────────────────────────────────────────────────────────────┘
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagChatService {

    private final IntentClassifierService intentClassifierService;
    private final GeminiService geminiService;
    private final CourseRepo courseRepo;
    private final TopicRepo topicRepo;
    private final RagChatSessionRepository chatSessionRepository;
    private final RagChatMessageRepository chatMessageRepository;

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    // ==================== MAIN ENTRY ====================

    @Transactional
    public RagChatResponse chat(RagChatRequest request) {
        String msg = request.getMessage();
        log.info("=== RAG Pipeline === Message: \"{}\"", msg);

        try {
            // 1. Session
            RagChatSession session = getOrCreateSession(request);
            saveMessage(session, MessageRole.USER, msg);

            // 2. Intent classification
            IntentResult intent = intentClassifierService.classifyIntent(msg);
            log.info("Intent: {} | Keyword: {} | Price: {}-{}", 
                    intent.getIntent(), intent.getKeyword(), intent.getMinPrice(), intent.getMaxPrice());

            // 3. Determine RAG mode
            RagMode mode = determineRagMode(intent);
            log.info("RAG Mode: {}", mode);

            // 4. Retrieve metadata from MySQL (always, for context)
            DbMetadata db = fetchMetadata(intent);
            log.info("DB: {} courses, {} topics", db.courses.size(), db.topics.size());

            // 5. Build context snippets
            List<String> contextSnippets = buildContextSnippets(db, intent);

            // 6. Generate response
            String answer = generateResponse(msg, contextSnippets, intent, db, mode);

            // 7. Calculate confidence
            double confidence = calcConfidence(intent, db, mode);

            // 8. Save & return
            saveMessage(session, MessageRole.ASSISTANT, answer);

            return RagChatResponse.success(
                    session.getId(), answer, intent.getIntent().toString(),
                    msg, confidence, contextSnippets);

        } catch (Exception e) {
            log.error("RAG Error: ", e);
            return RagChatResponse.error("Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại.");
        }
    }

    // ==================== RAG MODES ====================

    private enum RagMode {
        /** Truy xuất DB → inject vào prompt → Gemini trả lời dựa trên data */
        FULL_RAG,
        /** Gemini trả lời tự do + bổ sung data từ DB nếu liên quan */
        HYBRID,
        /** Gemini trả lời hoàn toàn tự do, kèm info hệ thống */
        FREE
    }

    private RagMode determineRagMode(IntentResult intent) {
        return switch (intent.getIntent()) {
            // Phải dùng data thực từ DB
            case COURSE_SEARCH, COURSE_RECOMMEND, PRICING_INFO, DISCOUNT_POLICY -> RagMode.FULL_RAG;
            // Có thể trả lời tự do nhưng kèm DB info
            case ENROLLMENT_INFO, PLATFORM_INFO -> RagMode.HYBRID;
            // Trả lời tự do (lộ trình, kiến thức, chào hỏi...)
            case GENERAL_CHAT -> RagMode.FREE;
        };
    }

    // ==================== DATA ====================

    private static class DbMetadata {
        List<Course> courses = new ArrayList<>();
        List<Topic> topics = new ArrayList<>();
        long totalCourses = 0;
    }

    // ==================== SESSION ====================

    private RagChatSession getOrCreateSession(RagChatRequest request) {
        String userId = getCurrentUserId();
        if (request.getSessionId() != null) {
            return chatSessionRepository.findById(request.getSessionId())
                    .orElseGet(() -> createSession(userId));
        }
        return createSession(userId);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }

    private RagChatSession createSession(String userId) {
        return chatSessionRepository.save(RagChatSession.builder().userId(userId).build());
    }

    private void saveMessage(RagChatSession session, MessageRole role, String content) {
        chatMessageRepository.save(RagChatMessage.builder()
                .session(session).role(role).content(content).build());
    }

    // ==================== METADATA RETRIEVAL ====================

    private DbMetadata fetchMetadata(IntentResult intent) {
        DbMetadata db = new DbMetadata();
        db.topics = topicRepo.findTopicByActiveTrue();
        db.totalCourses = courseRepo.findByActiveTrue().size();

        boolean hasPriceFilter = intent.getMaxPrice() != null || intent.getMinPrice() != null;

        switch (intent.getIntent()) {
            case COURSE_SEARCH -> {
                if (intent.getKeyword() != null && !intent.getKeyword().isBlank()) {
                    Page<Course> page = courseRepo.searchCourses(
                            intent.getKeyword(), null, PageRequest.of(0, 15));
                    db.courses = new ArrayList<>(page.getContent());
                } else if (hasPriceFilter) {
                    db.courses = new ArrayList<>(courseRepo.findByActiveTrue());
                } else {
                    db.courses = new ArrayList<>(courseRepo.findTop6ByActiveTrueOrderByCreatedAtDesc());
                }
            }
            case COURSE_RECOMMEND -> {
                if (intent.getKeyword() != null && !intent.getKeyword().isBlank()) {
                    Page<Course> page = courseRepo.searchCourses(
                            intent.getKeyword(), null, PageRequest.of(0, 10));
                    db.courses = new ArrayList<>(page.getContent());
                }
                if (db.courses.isEmpty()) {
                    db.courses = new ArrayList<>(courseRepo.findTop5ByActiveTrueOrderByEnrolledCountDesc());
                }
            }
            case PRICING_INFO, DISCOUNT_POLICY -> {
                if (intent.getKeyword() != null) {
                    Page<Course> page = courseRepo.searchCourses(
                            intent.getKeyword(), null, PageRequest.of(0, 15));
                    db.courses = new ArrayList<>(page.getContent());
                } else if (hasPriceFilter) {
                    db.courses = new ArrayList<>(courseRepo.findByActiveTrue());
                } else {
                    db.courses = new ArrayList<>(courseRepo.findTop6ByActiveTrueOrderByCreatedAtDesc());
                }
            }
            default -> {
                // GENERAL_CHAT, ENROLLMENT, PLATFORM - get a few for context
                if (intent.getKeyword() != null && !intent.getKeyword().isBlank()) {
                    Page<Course> page = courseRepo.searchCourses(
                            intent.getKeyword(), null, PageRequest.of(0, 5));
                    db.courses = new ArrayList<>(page.getContent());
                }
                if (db.courses.isEmpty()) {
                    db.courses = new ArrayList<>(courseRepo.findTop3ByActiveTrueOrderByCreatedAtDesc());
                }
            }
        }

        // Apply price filter
        if (hasPriceFilter) {
            db.courses = db.courses.stream()
                    .filter(c -> {
                        BigDecimal p = c.getPrice();
                        if (p == null) return false;
                        if (intent.getMaxPrice() != null && p.compareTo(intent.getMaxPrice()) > 0) return false;
                        if (intent.getMinPrice() != null && p.compareTo(intent.getMinPrice()) < 0) return false;
                        return true;
                    })
                    .limit(10)
                    .collect(Collectors.toList());
            log.info("Price filter applied: {} courses remain (max={}, min={})",
                    db.courses.size(), intent.getMaxPrice(), intent.getMinPrice());
        }

        return db;
    }

    // ==================== CONTEXT BUILDING ====================

    private List<String> buildContextSnippets(DbMetadata db, IntentResult intent) {
        List<String> snippets = new ArrayList<>();

        // System stats
        String topicNames = db.topics.stream().map(Topic::getName).collect(Collectors.joining(", "));
        snippets.add(String.format("[HỆ THỐNG] Course Shop: %d khóa học, %d chủ đề (%s)",
                db.totalCourses, db.topics.size(), topicNames));

        // Course details
        for (Course c : db.courses) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[KHÓA HỌC #%d] \"%s\"", c.getId(), c.getTitle()));
            if (c.getTopic() != null) sb.append(" | Chủ đề: ").append(c.getTopic().getName());
            if (c.getCreator() != null) sb.append(" | GV: ").append(c.getCreator().getFullname());
            sb.append(" | Giá: ").append(c.getPrice() != null
                    ? String.format("%,.0f VNĐ", c.getPrice().doubleValue()) : "Liên hệ");
            sb.append(" | ").append(c.getEnrolledCount() != null ? c.getEnrolledCount() : 0).append(" học viên");
            if (c.getDuration() != null) sb.append(" | ").append(c.getDuration()).append(" phút");
            if (c.getDescription() != null && !c.getDescription().isBlank()) {
                String desc = c.getDescription().length() > 120 ? c.getDescription().substring(0, 120) + "..." : c.getDescription();
                sb.append(" | Mô tả: ").append(desc);
            }
            if (c.getModules() != null && !c.getModules().isEmpty()) {
                sb.append(" | ").append(c.getModules().size()).append(" chương: ");
                sb.append(c.getModules().stream()
                        .sorted((a, b) -> Integer.compare(
                                a.getOrderIndex() != null ? a.getOrderIndex() : 0,
                                b.getOrderIndex() != null ? b.getOrderIndex() : 0))
                        .map(CourseModule::getTitle)
                        .collect(Collectors.joining(", ")));
            }
            snippets.add(sb.toString());
        }

        return snippets;
    }

    // ==================== RESPONSE GENERATION ====================

    private String generateResponse(String userMessage, List<String> context,
                                    IntentResult intent, DbMetadata db, RagMode mode) {
        // Always try Gemini first
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                String systemPrompt = buildSystemPrompt(intent, mode);
                String userPrompt = buildUserPrompt(userMessage, context, mode);

                String llmText = geminiService.generateResponse(systemPrompt, userPrompt, geminiApiKey);

                if (llmText != null && !llmText.isBlank()) {
                    StringBuilder html = new StringBuilder();
                    html.append("<div class='chat-ai-response'>\n");
                    html.append(markdownToHtml(llmText));
                    html.append("</div>\n");

                    // Append course cards for course-related intents
                    if (shouldShowCards(intent) && !db.courses.isEmpty()) {
                        html.append(renderCards(db.courses));
                    }

                    return html.toString();
                }
            } catch (Exception e) {
                log.warn("Gemini failed, using fallback: {}", e.getMessage());
            }
        }

        // Fallback khi Gemini không khả dụng
        return buildFallback(intent, db);
    }

    /**
     * System prompt khác nhau tùy RAG mode
     */
    private String buildSystemPrompt(IntentResult intent, RagMode mode) {
        return switch (mode) {
            case FULL_RAG -> """
                    Bạn là trợ lý AI thông minh của Course Shop — nền tảng khóa học trực tuyến.
                    
                    PHONG CÁCH: Thân thiện, chuyên nghiệp, nhiệt tình như một người bạn tư vấn giỏi.
                    Nói chuyện tự nhiên, dùng emoji phù hợp, có cảm xúc.
                    
                    QUY TẮC BẮT BUỘC (FULL RAG mode):
                    - Bạn PHẢI trả lời dựa trên dữ liệu thực từ database được cung cấp
                    - TUYỆT ĐỐI KHÔNG bịa tên khóa học, giá, giảng viên
                    - Khi giới thiệu khóa học: nêu rõ tên, giảng viên, giá, số học viên
                    - Nếu KHÔNG có khóa học phù hợp trong data, nói rõ ràng
                    - Có thể đưa ra nhận xét, phân tích, so sánh dựa trên data
                    
                    FORMAT: Dùng markdown (## heading, **bold**, - list items).
                    Trả lời bằng tiếng Việt, ngắn gọn nhưng đủ ý (100-200 từ).
                    """ + getIntentHint(intent);

            case HYBRID -> """
                    Bạn là trợ lý AI thông minh của Course Shop — nền tảng khóa học trực tuyến.
                    
                    PHONG CÁCH: Thân thiện, chuyên nghiệp như ChatGPT. Nói chuyện tự nhiên.
                    
                    QUY TẮC (HYBRID mode):
                    - Bạn có thể dùng kiến thức chung để trả lời
                    - NHƯNG nếu có data từ database, hãy ưu tiên sử dụng data thực
                    - Có thể bổ sung thông tin hữu ích từ kiến thức riêng
                    - KHÔNG bịa tên khóa học cụ thể, nhưng có thể gợi ý chủ đề
                    
                    FORMAT: Markdown. Tiếng Việt. Ngắn gọn nhưng đầy đủ.
                    """ + getIntentHint(intent);

            case FREE -> """
                    Bạn là trợ lý AI thông minh của Course Shop — nền tảng khóa học trực tuyến.
                    
                    PHONG CÁCH: Thân thiện, vui vẻ, nhiệt tình như ChatGPT.
                    Nói chuyện tự nhiên, có thể đùa nhẹ, dùng emoji.
                    
                    QUY TẮC (FREE mode):
                    - Bạn tự do trả lời bằng kiến thức riêng
                    - Có thể tư vấn lộ trình học, career advice, kiến thức tech
                    - Nếu người dùng hỏi về khóa học, hãy gợi ý họ hỏi cụ thể để bạn tìm
                    - Thông tin hệ thống được cung cấp ở dưới, dùng nếu cần
                    
                    FORMAT: Markdown. Tiếng Việt. Trả lời tự nhiên, có chiều sâu.
                    Nếu hỏi về lộ trình, có thể trả lời dài hơn (200-300 từ).
                    """;
        };
    }

    private String getIntentHint(IntentResult intent) {
        return switch (intent.getIntent()) {
            case COURSE_SEARCH -> "\nNgười dùng đang TÌM KIẾM khóa học. Phân tích nhu cầu → giới thiệu khóa phù hợp từ data.";
            case COURSE_RECOMMEND -> "\nNgười dùng muốn GỢI Ý khóa học. Phân tích → đề xuất khóa phù hợp nhất → giải thích lý do.";
            case PRICING_INFO -> "\nNgười dùng hỏi GIÁ. Liệt kê giá chính xác → so sánh → tư vấn ngân sách.";
            case DISCOUNT_POLICY -> "\nNgười dùng hỏi GIẢM GIÁ/KHUYẾN MÃI.";
            case ENROLLMENT_INFO -> "\nNgười dùng hỏi ĐĂNG KÝ. Hướng dẫn: Tạo tài khoản → Chọn khóa → Thanh toán VNPay → Bắt đầu học.";
            case PLATFORM_INFO -> "\nNgười dùng hỏi VỀ HỆ THỐNG. Giới thiệu Course Shop + stats thực.";
            case GENERAL_CHAT -> "";
        };
    }

    /**
     * User prompt khác nhau tùy RAG mode
     */
    private String buildUserPrompt(String userMessage, List<String> context, RagMode mode) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Người dùng hỏi: \"").append(userMessage).append("\"\n\n");

        if (!context.isEmpty()) {
            if (mode == RagMode.FULL_RAG) {
                prompt.append("📊 DỮ LIỆU THỰC TỪ DATABASE (BẮT BUỘC sử dụng):\n");
            } else {
                prompt.append("📊 Thông tin tham khảo từ hệ thống:\n");
            }
            for (String snippet : context) {
                prompt.append(snippet).append("\n");
            }
            prompt.append("\n");
        }

        if (mode == RagMode.FULL_RAG) {
            prompt.append("Hãy trả lời dựa trên data trên. KHÔNG bịa thông tin.\n");
        } else if (mode == RagMode.FREE) {
            prompt.append("Hãy trả lời tự do bằng kiến thức của bạn. Data trên chỉ để tham khảo.\n");
        }

        return prompt.toString();
    }

    // ==================== MARKDOWN → HTML ====================

    private String markdownToHtml(String text) {
        if (text == null) return "";
        // Headers
        text = text.replaceAll("(?m)^### (.+)$", "<h5>$1</h5>");
        text = text.replaceAll("(?m)^## (.+)$", "<h4>$1</h4>");
        text = text.replaceAll("(?m)^# (.+)$", "<h3>$1</h3>");
        // Bold & italic
        text = text.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");
        text = text.replaceAll("(?<![*])\\*(?![*])(.+?)(?<![*])\\*(?![*])", "<em>$1</em>");
        // List items
        text = text.replaceAll("(?m)^- (.+)", "<li>$1</li>");
        text = text.replaceAll("(?m)^\\* (.+)", "<li>$1</li>");
        text = text.replaceAll("(?m)^\\d+\\. (.+)", "<li>$1</li>");
        // Wrap consecutive <li> in <ul>
        text = text.replaceAll("((?:<li>.+?</li>\\s*)+)", "<ul>$1</ul>");
        // Paragraphs
        text = text.replaceAll("\n\n", "</p><p>");
        text = text.replaceAll("\n", "<br>");
        if (!text.startsWith("<")) text = "<p>" + text + "</p>";
        return text;
    }

    // ==================== COURSE CARDS ====================

    private boolean shouldShowCards(IntentResult intent) {
        // Show course cards for all intents EXCEPT pure info-only ones
        return intent.getIntent() != IntentType.ENROLLMENT_INFO
                && intent.getIntent() != IntentType.PLATFORM_INFO;
    }

    private String renderCards(List<Course> courses) {
        StringBuilder html = new StringBuilder("<div class='chat-course-results'>\n");
        for (Course c : courses) html.append(renderCard(c));
        html.append("</div>\n");
        return html.toString();
    }

    private String renderCard(Course c) {
        String img = (c.getCoverImage() != null && !c.getCoverImage().isEmpty())
                ? c.getCoverImage() : "/images/default-course.jpg";
        String topic = c.getTopic() != null ? c.getTopic().getName() : "N/A";
        String instructor = c.getCreator() != null ? c.getCreator().getFullname() : "N/A";
        String price = c.getPrice() != null
                ? String.format("%,d", c.getPrice().longValue()).replace(",", ".") : "0";
        long enrolled = c.getEnrolledCount() != null ? c.getEnrolledCount() : 0;

        return String.format("""
            <div class="course-card-chat" data-course-id="%d">
                <div class="course-card-image">
                    <img src="%s" alt="%s" onerror="this.src='/images/default-course.jpg'">
                    <span class="course-card-badge">%s</span>
                </div>
                <div class="course-card-content">
                    <h4 class="course-card-title">%s</h4>
                    <p class="course-card-instructor">👨‍🏫 %s</p>
                    <div class="course-card-stats">👥 %d học viên</div>
                    <div class="course-card-footer">
                        <div class="course-card-price">%s VNĐ</div>
                        <div class="course-card-actions">
                            <a href="/courses/%d" class="btn-course-detail" target="_blank">Chi tiết</a>
                            <a href="/courses/%d" class="btn-course-buy" target="_blank">Mua ngay</a>
                        </div>
                    </div>
                </div>
            </div>
            """, c.getId(), img, c.getTitle(), topic, c.getTitle(),
                instructor, enrolled, price, c.getId(), c.getId());
    }

    // ==================== CONFIDENCE ====================

    private double calcConfidence(IntentResult intent, DbMetadata db, RagMode mode) {
        double c = 0.5;
        if (mode == RagMode.FULL_RAG && !db.courses.isEmpty()) c += 0.25;
        else if (mode == RagMode.HYBRID) c += 0.15;
        else if (mode == RagMode.FREE) c += 0.1;
        if (intent.getKeyword() != null) c += 0.1;
        if (intent.getIntent() != IntentType.GENERAL_CHAT) c += 0.05;
        if (db.courses.size() >= 3) c += 0.05;
        return Math.min(c, 0.95);
    }

    // ==================== FALLBACK (khi Gemini không khả dụng) ====================

    private String buildFallback(IntentResult intent, DbMetadata db) {
        StringBuilder r = new StringBuilder();

        switch (intent.getIntent()) {
            case COURSE_SEARCH -> {
                if (db.courses.isEmpty()) {
                    r.append("<p>😔 Mình chưa tìm thấy khóa học nào");
                    if (intent.getKeyword() != null) r.append(" về <strong>").append(intent.getKeyword()).append("</strong>");
                    if (intent.getMaxPrice() != null) r.append(" dưới <strong>").append(fmt(intent.getMaxPrice())).append("</strong>");
                    r.append(" trong hệ thống.</p>");
                    r.append("<p>💡 Thử từ khóa khác hoặc xem <a href='/courses'>tất cả ").append(db.totalCourses).append(" khóa học</a>.</p>");
                } else {
                    r.append("<p>📚 Tìm thấy <strong>").append(db.courses.size()).append("</strong> khóa học");
                    if (intent.getKeyword() != null) r.append(" về <strong>").append(intent.getKeyword()).append("</strong>");
                    if (intent.getMaxPrice() != null) r.append(" dưới ").append(fmt(intent.getMaxPrice()));
                    r.append(" phù hợp:</p>");
                    r.append(renderCards(db.courses));
                    r.append("<p class='chat-hint'>👆 Click Chi tiết để xem nội dung!</p>");
                }
            }
            case COURSE_RECOMMEND -> {
                if (!db.courses.isEmpty()) {
                    r.append("<p>🎯 ");
                    if (intent.getKeyword() != null)
                        r.append("Với <strong>").append(intent.getKeyword()).append("</strong>, đ");
                    else r.append("Đ");
                    r.append("ây là những khóa học mình gợi ý cho bạn:</p>");
                    for (Course c : db.courses) {
                        r.append("<p>✅ <strong>").append(c.getTitle()).append("</strong> — ")
                                .append(c.getCreator() != null ? c.getCreator().getFullname() : "").append(" — ")
                                .append(c.getPrice() != null ? fmt(c.getPrice()) : "Liên hệ")
                                .append(" (").append(c.getEnrolledCount() != null ? c.getEnrolledCount() : 0).append(" học viên)</p>");
                    }
                    r.append(renderCards(db.courses));
                } else {
                    r.append("<p>🤔 Hãy cho mình biết thêm bạn muốn học gì nhé!</p>");
                }
            }
            case PRICING_INFO -> {
                if (!db.courses.isEmpty()) {
                    r.append("<p>💰 Bảng giá");
                    if (intent.getKeyword() != null) r.append(" khóa <strong>").append(intent.getKeyword()).append("</strong>");
                    r.append(":</p>");
                    for (Course c : db.courses) {
                        r.append("<p>• ").append(c.getTitle()).append(": <strong>")
                                .append(c.getPrice() != null ? fmt(c.getPrice()) : "Liên hệ").append("</strong></p>");
                    }
                    r.append(renderCards(db.courses));
                } else {
                    r.append("<p>Không có khóa học trong khoảng giá yêu cầu.</p>");
                }
            }
            case DISCOUNT_POLICY -> {
                r.append("<p>🎁 <strong>Chính sách ưu đãi Course Shop:</strong></p>");
                r.append("<p>• 🎉 Giảm giá đặc biệt cho sinh viên<br>");
                r.append("• 🔥 Flash sale hàng tháng<br>");
                r.append("• 💝 Combo ưu đãi mua nhiều khóa<br>");
                r.append("• 🎁 Mã giảm giá cho thành viên mới</p>");
                r.append("<p>📧 Liên hệ support@courseshop.vn để nhận ưu đãi!</p>");
            }
            case ENROLLMENT_INFO -> {
                r.append("<p>✅ <strong>Đăng ký chỉ 4 bước:</strong></p>");
                r.append("<p>1️⃣ Tạo tài khoản miễn phí<br>");
                r.append("2️⃣ Chọn khóa học yêu thích<br>");
                r.append("3️⃣ Thanh toán qua VNPay<br>");
                r.append("4️⃣ Bắt đầu học ngay! 🎓</p>");
                r.append("<p>Hiện có <strong>").append(db.totalCourses).append("</strong> khóa học đang mở đăng ký!</p>");
            }
            case PLATFORM_INFO -> {
                r.append("<p>ℹ️ <strong>Course Shop</strong> — Nền tảng học trực tuyến</p>");
                r.append("<p>📊 <strong>").append(db.totalCourses).append("</strong> khóa học | <strong>")
                        .append(db.topics.size()).append("</strong> chủ đề</p>");
                r.append("<p>📂 ").append(db.topics.stream().map(Topic::getName).collect(Collectors.joining(", "))).append("</p>");
                r.append("<p>👨‍🏫 Giảng viên chuyên nghiệp | 📜 Chứng chỉ | 💳 VNPay an toàn</p>");
            }
            case GENERAL_CHAT -> {
                r.append("<p>👋 Chào bạn! Mình là trợ lý AI của <strong>Course Shop</strong>.</p>");
                r.append("<p>Mình có thể giúp bạn:</p>");
                r.append("<p>🔍 Tìm khóa học phù hợp<br>");
                r.append("💡 Gợi ý & tư vấn lộ trình<br>");
                r.append("💰 Thông tin giá & ưu đãi<br>");
                r.append("📝 Hướng dẫn đăng ký</p>");
                r.append("<p>Hỏi mình bất cứ điều gì nhé! 😊</p>");
            }
        }

        return r.toString();
    }

    private String fmt(BigDecimal price) {
        return String.format("%,.0f VNĐ", price.doubleValue());
    }

    // ==================== HISTORY ====================

    public List<RagChatMessage> getHistory(Long sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}
