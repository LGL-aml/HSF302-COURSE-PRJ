package com.jungle.courseshop.service.rag;

import com.jungle.courseshop.dto.rag.IntentResult;
import com.jungle.courseshop.dto.rag.RagChatRequest;
import com.jungle.courseshop.dto.rag.RagChatResponse;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.rag.RagChatMessage;
import com.jungle.courseshop.entity.rag.RagChatMessage.MessageRole;
import com.jungle.courseshop.entity.rag.RagChatSession;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.rag.RagChatMessageRepository;
import com.jungle.courseshop.repository.rag.RagChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core RAG Chat Service - RAG Pipeline Orchestrator
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagChatService {

    private final IntentClassifierService intentClassifierService;
    private final CourseRepo courseRepo;
    private final RagChatSessionRepository chatSessionRepository;
    private final RagChatMessageRepository chatMessageRepository;

    @Transactional
    public RagChatResponse chat(RagChatRequest request) {
        log.info("Processing RAG chat request: {}", request.getMessage());

        try {
            // 1. Get or create session
            RagChatSession session = getOrCreateSession(request);

            // 2. Save user message
            saveMessage(session, MessageRole.USER, request.getMessage());

            // 3. Classify intent
            IntentResult intent = intentClassifierService.classifyIntent(request.getMessage());
            log.info("Detected intent: {}", intent.getIntent());

            // 4. Retrieve relevant data based on intent
            String context = retrieveContext(intent);

            // 5. Build RAG prompt and call LLM
            String response = generateResponse(request.getMessage(), context, intent);

            // 6. Save assistant message
            saveMessage(session, MessageRole.ASSISTANT, response);

            return RagChatResponse.success(session.getId(), response, intent.getIntent().toString());

        } catch (Exception e) {
            log.error("Error processing RAG chat request", e);
            return RagChatResponse.error(e.getMessage());
        }
    }

    private RagChatSession getOrCreateSession(RagChatRequest request) {
        // Get current authenticated user
        String userId = getCurrentUserId();
        
        if (request.getSessionId() != null) {
            return chatSessionRepository.findById(request.getSessionId())
                .orElseGet(() -> createNewSession(userId));
        }
        return createNewSession(userId);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    private RagChatSession createNewSession(String userId) {
        RagChatSession session = RagChatSession.builder()
            .userId(userId)
            .build();
        return chatSessionRepository.save(session);
    }

    private void saveMessage(RagChatSession session, MessageRole role, String content) {
        RagChatMessage message = RagChatMessage.builder()
            .session(session)
            .role(role)
            .content(content)
            .build();
        chatMessageRepository.save(message);
    }

    private String retrieveContext(IntentResult intent) {
        StringBuilder context = new StringBuilder();

        switch (intent.getIntent()) {
            case COURSE_SEARCH -> {
                // Combine semantic search with structured query
                List<Course> courses = retrieveCourses(intent);

                context.append("<div class='chat-course-results'>\n");

                // Add courses from structured query
                if (!courses.isEmpty()) {
                    for (Course c : courses) {
                        context.append(formatCourseCard(c)).append("\n");
                    }
                } else {
                    context.append("<p class='no-results'>Không tìm thấy khóa học phù hợp.</p>\n");
                }
                
                context.append("</div>\n");
            }

            case COURSE_RECOMMEND -> {
                // Get recommended courses
                List<Course> recommendedCourses;
                
                if (intent.getTargetAudience() != null) {
                    // Filter by level if specified
                    recommendedCourses = courseRepo.findTop6ByActiveTrueOrderByCreatedAtDesc();
                } else {
                    // Default: popular courses
                    recommendedCourses = courseRepo.findTop5ByActiveTrueOrderByEnrolledCountDesc();
                }
                
                context.append("<div class='chat-course-results'>\n");
                
                if (!recommendedCourses.isEmpty()) {
                    for (Course c : recommendedCourses) {
                        context.append(formatCourseCard(c)).append("\n");
                    }
                } else {
                    context.append("<p class='no-results'>Chưa có gợi ý phù hợp.</p>\n");
                }
                
                context.append("</div>\n");
            }

            case PRICING_INFO -> {
                List<Course> courses = retrieveCourses(intent);
                context.append("<div class='chat-course-results'>\n");
                if (!courses.isEmpty()) {
                    for (Course c : courses) {
                        context.append(formatCourseCard(c)).append("\n");
                    }
                } else {
                    context.append("<p class='no-results'>Không tìm thấy thông tin giá.</p>\n");
                }
                context.append("</div>\n");
            }

            case ENROLLMENT_INFO -> {
                // No course cards needed, just text
                context.append("");
            }

            case PLATFORM_INFO -> {
                // No course cards needed, just text
                context.append("");
            }

            case GENERAL_CHAT -> {
                // For general chat, no specific context needed
                context.append("");
            }
        }

        return context.toString();
    }

    private List<Course> retrieveCourses(IntentResult intent) {
        List<Course> courses;
        
        // Determine if we need to fetch more courses for price filtering
        boolean hasPriceFilter = (intent.getMaxPrice() != null || intent.getMinPrice() != null);
        
        // Get courses based on keyword or default
        if (intent.getKeyword() != null) {
            Page<Course> coursePage = courseRepo.searchCourses(
                intent.getKeyword(), 
                null, 
                PageRequest.of(0, 20)  // Get more results for filtering
            );
            courses = coursePage.getContent();
        } else if (hasPriceFilter) {
            // If filtering by price without keyword, get all active courses
            courses = courseRepo.findByActiveTrue();
        } else {
            // Default: return top recent courses
            courses = courseRepo.findTop6ByActiveTrueOrderByCreatedAtDesc();
        }
        
        // Apply price filtering
        if (hasPriceFilter) {
            courses = courses.stream()
                .filter(course -> {
                    BigDecimal price = course.getPrice();
                    if (price == null) return false;
                    
                    // Check max price constraint
                    if (intent.getMaxPrice() != null && price.compareTo(intent.getMaxPrice()) > 0) {
                        return false;
                    }
                    
                    // Check min price constraint
                    if (intent.getMinPrice() != null && price.compareTo(intent.getMinPrice()) < 0) {
                        return false;
                    }
                    
                    return true;
                })
                .limit(10)  // Limit to 10 results after filtering
                .collect(Collectors.toList());
        }
        
        return courses;
    }

    /**
     * Format course as interactive HTML card with price and action buttons
     */
    private String formatCourseCard(Course course) {
        String imageUrl = course.getCoverImage() != null && !course.getCoverImage().isEmpty() 
            ? course.getCoverImage() 
            : "/images/default-course.jpg";
        
        String topicName = course.getTopic() != null ? course.getTopic().getName() : "Chưa phân loại";
        String instructorName = course.getCreator() != null ? course.getCreator().getFullname() : "Chưa xác định";
        
        // Format price with thousands separator (BigDecimal to long)
        String formattedPrice = String.format("%,d", course.getPrice().longValue()).replace(",", ".");
        
        return String.format("""
            <div class="course-card-chat" data-course-id="%d">
                <div class="course-card-image">
                    <img src="%s" alt="%s" onerror="this.src='/images/default-course.jpg'">
                    <span class="course-card-badge">%s</span>
                </div>
                <div class="course-card-content">
                    <h4 class="course-card-title">%s</h4>
                    <p class="course-card-instructor"><i class="fas fa-user"></i> %s</p>
                    <div class="course-card-stats">
                        <span><i class="fas fa-users"></i> %d học viên</span>
                    </div>
                    <div class="course-card-footer">
                        <div class="course-card-price">%s VNĐ</div>
                        <div class="course-card-actions">
                            <a href="/courses/%d" class="btn-course-detail" target="_blank">
                                <i class="fas fa-eye"></i> Chi tiết
                            </a>
                            <a href="/courses/%d" class="btn-course-buy" target="_blank">
                                <i class="fas fa-shopping-cart"></i> Mua ngay
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            """,
            course.getId(),
            imageUrl,
            course.getTitle(),
            topicName,
            course.getTitle(),
            instructorName,
            course.getEnrolledCount(),
            formattedPrice,
            course.getId(),
            course.getId()
        );
    }

    private String generateResponse(String userMessage, String context, IntentResult intent) {
        // Use rule-based fallback responses directly (LLM API disabled due to persistent errors)
        log.info("Generating response using rule-based templates for intent: {}", intent.getIntent());
        return generateFallbackResponse(context, intent);
    }
    
    /**
     * Generate a fallback response when LLM API is unavailable
     */
    private String generateFallbackResponse(String context, IntentResult intent) {
        StringBuilder response = new StringBuilder();
        
        switch (intent.getIntent()) {
            case COURSE_SEARCH -> {
                response.append("<div class='chat-response-header'>📚 <strong>Kết quả tìm kiếm khóa học</strong></div>\n");
                if (context.contains("no-results")) {
                    response.append("<p>Xin lỗi, hiện tại chúng tôi chưa có khóa học phù hợp với yêu cầu của bạn.</p>\n");
                    response.append("<p><strong>Bạn có thể:</strong></p>\n");
                    response.append("<ul>\n");
                    response.append("<li>Thử tìm kiếm với từ khóa khác</li>\n");
                    response.append("<li>Xem <a href='/courses' target='_blank'>tất cả khóa học</a></li>\n");
                    response.append("<li>Liên hệ support: <strong>1900-8888</strong></li>\n");
                    response.append("</ul>\n");
                } else {
                    response.append(context);
                    response.append("<p class='chat-hint'>💡 Click <strong>Chi tiết</strong> để xem thêm hoặc <strong>Mua ngay</strong> để đăng ký!</p>\n");
                }
            }
            
            case COURSE_RECOMMEND -> {
                response.append("<div class='chat-response-header'>🎯 <strong>Gợi ý khóa học cho bạn</strong></div>\n");
                response.append(context);
                response.append("<p class='chat-hint'>✨ Các khóa học trên rất phù hợp với nhu cầu của bạn. Đăng ký ngay để nhận ưu đãi!</p>\n");
            }
            
            case PRICING_INFO -> {
                response.append("<div class='chat-response-header'>💰 <strong>Thông tin giá khóa học</strong></div>\n");
                response.append(context);
                response.append("<p class='chat-hint'>💡 Giá đã bao gồm toàn bộ nội dung khóa học và chứng chỉ hoàn thành!</p>\n");
            }
            
            case ENROLLMENT_INFO -> {
                response.append("<div class='chat-response-header'>✅ <strong>Hướng dẫn đăng ký</strong></div>\n");
                response.append("<div class='chat-steps'>\n");
                response.append("<p><strong>Các bước đăng ký khóa học:</strong></p>\n");
                response.append("<ol>\n");
                response.append("<li>📝 Tạo tài khoản miễn phí trên Course Shop</li>\n");
                response.append("<li>🔍 Chọn khóa học và thêm vào giỏ hàng</li>\n");
                response.append("<li>💳 Thanh toán qua VNPay, Momo, hoặc thẻ tín dụng</li>\n");
                response.append("<li>🎓 Truy cập khóa học ngay sau khi thanh toán thành công</li>\n");
                response.append("</ol>\n");
                response.append("<p class='chat-hint'>🚀 Bắt đầu học ngay hôm nay!</p>\n");
                response.append("</div>\n");
            }
            
            case PLATFORM_INFO -> {
                response.append("<div class='chat-response-header'>ℹ️ <strong>Thông tin Course Shop</strong></div>\n");
                response.append("<div class='chat-info-box'>\n");
                response.append("<p><strong>Nền tảng học trực tuyến hàng đầu Việt Nam</strong></p>\n");
                response.append("<ul>\n");
                response.append("<li>🎓 Hơn 1000+ khóa học chất lượng cao</li>\n");
                response.append("<li>👨‍🏫 Giảng viên giàu kinh nghiệm</li>\n");
                response.append("<li>📜 Chứng chỉ sau khi hoàn thành</li>\n");
                response.append("<li>💬 Hỗ trợ 24/7 qua hotline và email</li>\n");
                response.append("<li>🔄 Cập nhật nội dung liên tục</li>\n");
                response.append("</ul>\n");
                response.append("<p class='chat-hint'>✨ Chúng tôi luôn sẵn sàng hỗ trợ bạn!</p>\n");
                response.append("</div>\n");
            }
            
            case GENERAL_CHAT -> {
                response.append("<p>👋 <strong>Xin chào!</strong> Tôi có thể giúp bạn:</p>\n");
                response.append("<ul>\n");
                response.append("<li>🔍 Tìm kiếm và gợi ý khóa học phù hợp</li>\n");
                response.append("<li>💰 Tư vấn giá cả và thông tin khóa học</li>\n");
                response.append("<li>📝 Hướng dẫn đăng ký và thanh toán</li>\n");
                response.append("<li>ℹ️ Thông tin về nền tảng Course Shop</li>\n");
                response.append("</ul>\n");
                response.append("<p class='chat-hint'>💬 Hãy cho tôi biết bạn quan tâm đến khóa học nào nhé! 😊</p>\n");
            }
        }
        
        // Add support info at the end
        response.append("<div class='chat-support-footer'>\n");
        response.append("<p>📞 <strong>Liên hệ hỗ trợ:</strong> Hotline <strong>1900-8888</strong> (24/7) | Email <strong>support@courseshop.vn</strong></p>\n");
        response.append("</div>\n");
        
        return response.toString();
    }

    /**
     * Get conversation history for a session
     */
    public List<RagChatMessage> getHistory(Long sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}
