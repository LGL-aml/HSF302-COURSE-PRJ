package com.jungle.courseshop.service.rag;

import com.jungle.courseshop.dto.rag.IntentResult;
import com.jungle.courseshop.dto.rag.IntentResult.IntentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for classifying user intent and extracting metadata from user messages.
 * 
 * Supports:
 * - Intent classification (COURSE_SEARCH, PRICING_INFO, etc.)
 * - Price extraction: "200k", "dưới 500k", "từ 100k đến 500k", "1 triệu"
 * - Keyword extraction: technology names, Vietnamese course keywords
 * - Target audience: beginner, intermediate, advanced
 * - Quantity: "top 5", "3 khóa học"
 */
@Service
@Slf4j
public class IntentClassifierService {

    /**
     * Classify user intent and extract all metadata from the message.
     */
    public IntentResult classifyIntent(String userMessage) {
        log.info("Classifying intent for: {}", userMessage);
        
        String lowerMessage = userMessage.toLowerCase().trim();
        IntentResult.IntentResultBuilder builder = IntentResult.builder().rawQuery(userMessage);
        
        // Extract metadata from message
        extractPriceInfo(lowerMessage, builder);
        extractQuantity(lowerMessage, builder);
        extractTargetAudience(lowerMessage, builder);
        
        String keyword = extractKeyword(lowerMessage);
        if (keyword != null) {
            builder.keyword(keyword);
        }
        
        // Determine intent
        IntentType intent = determineIntent(lowerMessage);
        builder.intent(intent);
        
        IntentResult result = builder.build();
        log.info("Intent: {} | Keyword: {} | MaxPrice: {} | MinPrice: {} | Audience: {}",
                result.getIntent(), result.getKeyword(), result.getMaxPrice(), 
                result.getMinPrice(), result.getTargetAudience());
        return result;
    }
    
    // ==================== INTENT DETERMINATION ====================
    
    private IntentType determineIntent(String message) {
        // Priority order: most specific first → least specific last
        
        // 1. DISCOUNT_POLICY
        if (containsAny(message, "giảm giá", "khuyến mãi", "voucher", "mã giảm", 
                        "sale", "discount", "coupon", "ưu đãi", "khuyến mại", "promotion")) {
            return IntentType.DISCOUNT_POLICY;
        }
        
        // 2. PRICING_INFO: explicit price questions
        if (containsAny(message, "bao nhiêu tiền", "chi phí", "miễn phí", "giá bao nhiêu")) {
            return IntentType.PRICING_INFO;
        }
        
        // 3. PRICING_INFO: price filter keywords (dưới Xk, trên Xk, rẻ, mắc)
        if (containsAny(message, "dưới", "trên", "rẻ", "mắc", "tối đa", "tối thiểu")) {
            return IntentType.PRICING_INFO;
        }
        
        // 4. ENROLLMENT_INFO
        if (containsAny(message, "đăng ký", "ghi danh", "thanh toán", "chứng chỉ", 
                        "certificate", "enroll", "payment", "đăng kí", "cách mua")) {
            return IntentType.ENROLLMENT_INFO;
        }
        
        // 5. PLATFORM_INFO
        if (containsAny(message, "nền tảng", "platform", "giảng viên", "lecturer", 
                        "hỗ trợ", "support", "liên hệ", "contact", "course shop là gì")) {
            return IntentType.PLATFORM_INFO;
        }
        
        // 6. GENERAL_CHAT (FREE mode) — MUST check BEFORE COURSE_SEARCH/RECOMMEND
        //    Đây là câu hỏi kiến thức, lộ trình, career, không phải tìm khóa cụ thể
        if (containsAny(message, "lộ trình", "roadmap", "career", "hướng đi", "con đường",
                        "bắt đầu từ đâu", "nên bắt đầu", "học gì trước", "thứ tự học",
                        "là gì", "so sánh", "khác nhau", "giải thích", "tại sao",
                        "cách học", "kinh nghiệm", "mẹo", "tips", "tài liệu",
                        "sự nghiệp", "lương", "salary", "công việc", "job",
                        "xu hướng", "trend", "tương lai", "triển vọng")) {
            return IntentType.GENERAL_CHAT;
        }
        
        // 7. COURSE_RECOMMEND: "nên học khóa nào", "gợi ý khóa"
        if (containsAny(message, "gợi ý", "recommend", "suggest", "nên học", "phù hợp", 
                        "tốt nhất", "best", "top", "phổ biến", "hot",
                        "học khóa nào", "khóa nào hay")) {
            return IntentType.COURSE_RECOMMEND;
        }
        
        // 8. Price mention in message → PRICING_INFO (e.g., "200k mua gì", "500k được gì")
        if (hasPriceMention(message)) {
            return IntentType.PRICING_INFO;
        }
        
        // 9. "giá" or "mua" alone → PRICING_INFO
        if (containsAny(message, "giá", "mua")) {
            return IntentType.PRICING_INFO;
        }
        
        // 10. COURSE_SEARCH: tìm/kiếm/khóa học cụ thể
        if (containsAny(message, "tìm", "search", "có khóa", "khóa học", "course", "kiếm")) {
            return IntentType.COURSE_SEARCH;
        }
        
        // 11. If has tech keyword + "học" → COURSE_SEARCH (e.g., "học java", "học python")
        if (hasTechKeyword(message) && message.contains("học")) {
            return IntentType.COURSE_SEARCH;
        }
        
        // 12. If only has tech keyword without "học" → could be general question
        if (hasTechKeyword(message)) {
            return IntentType.GENERAL_CHAT;
        }
        
        return IntentType.GENERAL_CHAT;
    }
    
    // ==================== KEYWORD EXTRACTION ====================
    
    private String extractKeyword(String message) {
        // Technology/topic keywords (check these first - more specific)
        String[] techTopics = {
            "java", "python", "javascript", "typescript", "react", "angular", "vue", 
            "spring", "spring boot", "node", "nodejs", "node.js",
            "docker", "kubernetes", "aws", "azure", "gcp", "devops", "ci/cd",
            "sql", "mysql", "mongodb", "postgresql", "redis",
            "api", "rest", "restful", "graphql", "microservice", "microservices",
            "frontend", "backend", "fullstack", "full-stack", "full stack",
            "web", "mobile", "android", "ios", "flutter", "react native",
            "machine learning", "deep learning", "data science", "ai", "nlp",
            "blockchain", "crypto", "solidity",
            "c#", "c++", ".net", "dotnet", "php", "laravel", "ruby", "golang", "go", "rust",
            "html", "css", "sass", "tailwind", "bootstrap",
            "git", "linux", "unity", "unreal", "game",
            "figma", "photoshop", "design", "ui/ux", "ux", "ui",
            // Vietnamese tech terms
            "lập trình", "cơ sở dữ liệu", "trí tuệ nhân tạo", "bảo mật", "mạng",
            "phần mềm", "ứng dụng", "website", "app"
        };
        
        // Match longest keyword first (e.g., "spring boot" before "spring")
        String found = null;
        for (String topic : techTopics) {
            if (message.contains(topic)) {
                if (found == null || topic.length() > found.length()) {
                    found = topic;
                }
            }
        }
        if (found != null) return found;
        
        // Try to extract Vietnamese keyword after "khóa học"
        String[] skipWords = {"dưới", "trên", "từ", "đến", "tối", "đa", "thiểu", "giá", 
                              "rẻ", "mắc", "miễn", "phí", "k", "triệu", "nghìn", "ngàn",
                              "nào", "gì", "nên", "có", "cho", "về", "của", "với", "và",
                              "tốt", "nhất", "hot", "mới", "cơ", "bản"};
        
        // Pattern: "khóa học <keyword>"
        Pattern vnPattern = Pattern.compile("khóa\\s+học\\s+(\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = vnPattern.matcher(message);
        if (matcher.find()) {
            String potentialKeyword = matcher.group(1).toLowerCase();
            // Remove price suffix if attached (e.g., "java200k" shouldn't happen but safety)
            potentialKeyword = potentialKeyword.replaceAll("\\d+[kK]?$", "").trim();
            if (!potentialKeyword.isEmpty()) {
                for (String skip : skipWords) {
                    if (potentialKeyword.equals(skip)) return null;
                }
                return potentialKeyword;
            }
        }
        
        // Pattern: "học <keyword>"  
        Pattern learnPattern = Pattern.compile("(?:học|tìm|kiếm|tìm kiếm)\\s+(\\S+)", Pattern.CASE_INSENSITIVE);
        matcher = learnPattern.matcher(message);
        if (matcher.find()) {
            String potentialKeyword = matcher.group(1).toLowerCase()
                    .replaceAll("\\d+[kK]?$", "").trim();
            if (!potentialKeyword.isEmpty()) {
                for (String skip : skipWords) {
                    if (potentialKeyword.equals(skip)) return null;
                }
                // Only return if it looks like a meaningful keyword (not a common word)
                if (potentialKeyword.length() >= 2) {
                    return potentialKeyword;
                }
            }
        }
        
        return null;
    }
    
    // ==================== PRICE EXTRACTION ====================
    
    /**
     * Extract price information from message.
     * 
     * BUG FIX: When a price is mentioned without explicit context words (dưới/trên),
     * DEFAULT to maxPrice. E.g., "khóa học java 200k" → maxPrice = 200,000
     * This is the most natural interpretation: user wants courses UP TO that price.
     */
    private void extractPriceInfo(String message, IntentResult.IntentResultBuilder builder) {
        Pattern pricePattern = Pattern.compile("(\\d+)\\s*(k|triệu|tr|nghìn|ngàn)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pricePattern.matcher(message);
        
        List<BigDecimal> prices = new ArrayList<>();
        
        while (matcher.find()) {
            String number = matcher.group(1);
            String unit = matcher.group(2).toLowerCase();
            
            BigDecimal price = new BigDecimal(number);
            if (unit.equals("k") || unit.contains("nghìn") || unit.contains("ngàn")) {
                price = price.multiply(new BigDecimal("1000"));
            } else if (unit.contains("triệu") || unit.equals("tr")) {
                price = price.multiply(new BigDecimal("1000000"));
            }
            prices.add(price);
        }
        
        if (prices.isEmpty()) return;
        
        // Determine context for price assignment
        boolean hasUnder = containsAny(message, "dưới", "tối đa", "max", "không quá", "<=", "nhỏ hơn");
        boolean hasOver = containsAny(message, "trên", "tối thiểu", "min", "ít nhất", ">=", "lớn hơn", "từ");
        boolean hasRange = containsAny(message, "đến") && (containsAny(message, "từ") || prices.size() >= 2);
        
        if (hasRange && prices.size() >= 2) {
            // Range: "từ 100k đến 500k"
            builder.minPrice(prices.get(0));
            builder.maxPrice(prices.get(1));
            log.info("Price range detected: {} - {}", prices.get(0), prices.get(1));
        } else if (hasUnder) {
            // Under: "dưới 500k"
            builder.maxPrice(prices.get(0));
            log.info("Max price detected: {}", prices.get(0));
        } else if (hasOver) {
            // Over: "trên 200k"
            builder.minPrice(prices.get(0));
            log.info("Min price detected: {}", prices.get(0));
        } else {
            // DEFAULT: No explicit context → treat as maxPrice
            // "khóa học java 200k" → user wants courses up to 200k
            builder.maxPrice(prices.get(0));
            log.info("Price without context → defaulting to maxPrice: {}", prices.get(0));
        }
    }
    
    // ==================== QUANTITY & AUDIENCE ====================
    
    private void extractQuantity(String message, IntentResult.IntentResultBuilder builder) {
        Pattern qtyPattern = Pattern.compile("(\\d+)\\s*(khóa|course|top)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = qtyPattern.matcher(message);
        if (matcher.find()) {
            builder.quantity(Integer.parseInt(matcher.group(1)));
        }
        // Also check "top X"
        Pattern topPattern = Pattern.compile("top\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        matcher = topPattern.matcher(message);
        if (matcher.find()) {
            builder.quantity(Integer.parseInt(matcher.group(1)));
        }
    }
    
    private void extractTargetAudience(String message, IntentResult.IntentResultBuilder builder) {
        if (containsAny(message, "beginner", "cơ bản", "mới bắt đầu", "newbie", "người mới", "nhập môn")) {
            builder.targetAudience("beginner");
        } else if (containsAny(message, "intermediate", "trung cấp", "trung bình")) {
            builder.targetAudience("intermediate");
        } else if (containsAny(message, "advanced", "nâng cao", "chuyên sâu", "expert", "pro")) {
            builder.targetAudience("advanced");
        }
    }
    
    // ==================== HELPERS ====================
    
    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasPriceMention(String message) {
        return Pattern.compile("\\d+\\s*(k|triệu|tr|nghìn|ngàn)", Pattern.CASE_INSENSITIVE)
                .matcher(message).find();
    }
    
    private boolean hasCourseMention(String message) {
        return containsAny(message, "khóa", "course", "học", "learn");
    }
    
    private boolean hasTechKeyword(String message) {
        String[] techs = {"java", "python", "javascript", "react", "angular", "spring", 
                          "docker", "aws", "sql", "web", "mobile", "ai", "c#", "php"};
        return containsAny(message, techs);
    }
}
