package com.jungle.courseshop.service.rag;

import com.jungle.courseshop.dto.rag.IntentResult;
import com.jungle.courseshop.dto.rag.IntentResult.IntentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for classifying user intent using rule-based keyword matching
 */
@Service
@Slf4j
public class IntentClassifierService {


    /**
     * Classify user intent using rule-based keyword matching (no external API needed)
     */
    public IntentResult classifyIntent(String userMessage) {
        log.info("Classifying intent for: {}", userMessage);
        
        String lowerMessage = userMessage.toLowerCase().trim();
        IntentResult.IntentResultBuilder builder = IntentResult.builder().rawQuery(userMessage);
        
        // Extract price information
        extractPriceInfo(lowerMessage, builder);
        
        // Extract quantity
        extractQuantity(lowerMessage, builder);
        
        // Extract target audience
        extractTargetAudience(lowerMessage, builder);
        
        // Extract keyword (course name/topic)
        String keyword = extractKeyword(lowerMessage);
        if (keyword != null) {
            builder.keyword(keyword);
        }
        
        // Determine intent based on keywords
        IntentType intent = determineIntent(lowerMessage);
        builder.intent(intent);
        
        log.info("Classified intent: {} for message: {}", intent, userMessage);
        return builder.build();
    }
    
    private IntentType determineIntent(String message) {
        // Priority order: Check specific intents first, then general
        
        // PRICING_INFO: giá, chi phí, miễn phí, phí, tiền, dưới Xk, trên Xk, giảm giá, khuyến mãi
        if (containsAny(message, "giá", "chi phí", "miễn phí", "bao nhiêu tiền", "phí", "tốn", "trả", 
                        "dưới", "trên", "rẻ", "mắc", "giảm giá", "khuyến mãi", "voucher", "mã giảm", 
                        "sale", "discount", "coupon", "ưu đãi")) {
            return IntentType.PRICING_INFO;
        }
        
        // ENROLLMENT_INFO: đăng ký, thanh toán, chứng chỉ, ghi danh
        if (containsAny(message, "đăng ký", "ghi danh", "thanh toán", "chứng chỉ", "certificate", "enroll", "payment", "đăng kí")) {
            return IntentType.ENROLLMENT_INFO;
        }
        
        // PLATFORM_INFO: nền tảng, giảng viên, hỗ trợ, support
        if (containsAny(message, "nền tảng", "platform", "giảng viên", "lecturer", "hỗ trợ", "support", "liên hệ", "contact")) {
            return IntentType.PLATFORM_INFO;
        }
        
        // COURSE_RECOMMEND: gợi ý, recommend, suggest, nên học
        if (containsAny(message, "gợi ý", "recommend", "suggest", "nên học", "phù hợp", "tốt nhất", "best", "top")) {
            return IntentType.COURSE_RECOMMEND;
        }
        
        // COURSE_SEARCH: tìm, search, có khóa, learn, học
        if (containsAny(message, "tìm", "search", "có khóa", "khóa học", "course", "học", "learn", "java", "python", "react", "angular", "spring")) {
            return IntentType.COURSE_SEARCH;
        }
        
        // Default to GENERAL_CHAT
        return IntentType.GENERAL_CHAT;
    }
    
    private String extractKeyword(String message) {
        // Common course topics/technologies
        String[] topics = {
            "java", "python", "javascript", "react", "angular", "vue", "spring", "node",
            "docker", "kubernetes", "aws", "azure", "devops", "sql", "mysql", "mongodb",  
            "api", "rest", "microservice", "frontend", "backend", "fullstack", "web", "mobile",
            "android", "ios", "flutter", "machine learning", "data science", "ai", "blockchain"
        };
        
        for (String topic : topics) {
            if (message.contains(topic)) {
                return topic;
            }
        }
        
        // Skip price-related and filter-related words
        String[] skipWords = {"dưới", "trên", "từ", "đến", "tối", "đa", "thiểu", "giá", "rẻ", "mắc", 
                              "miễn", "phí", "k", "triệu", "nghìn", "ngàn"};
        
        // Extract Vietnamese keywords (e.g., "khóa học Java")
        Pattern vnPattern = Pattern.compile("khóa học\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = vnPattern.matcher(message);
        if (matcher.find()) {
            String potentialKeyword = matcher.group(1).toLowerCase();
            
            // Check if it's not a skip word
            for (String skip : skipWords) {
                if (potentialKeyword.equals(skip)) {
                    return null;  // Don't use price/filter words as course keywords
                }
            }
            
            return potentialKeyword;
        }
        
        return null;
    }
    
    private void extractPriceInfo(String message, IntentResult.IntentResultBuilder builder) {
        // Extract price ranges: "dưới 500k", "từ 100k đến 500k", "dưới 1 triệu"
        Pattern pricePattern = Pattern.compile("(\\d+)\\s*(k|triệu|tr|nghìn|ngàn)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pricePattern.matcher(message);
        
        while (matcher.find()) {
            String number = matcher.group(1);
            String unit = matcher.group(2).toLowerCase();
            
            BigDecimal price = new BigDecimal(number);
            if (unit.equals("k") || unit.contains("nghìn") || unit.contains("ngàn")) {
                price = price.multiply(new BigDecimal("1000"));
            } else if (unit.contains("triệu") || unit.equals("tr")) {
                price = price.multiply(new BigDecimal("1000000"));
            }
            
            // Determine if it's max or min based on context
            if (message.contains("dưới") || message.contains("tối đa") || message.contains("max")) {
                builder.maxPrice(price);
            } else if (message.contains("trên") || message.contains("tối thiểu") || message.contains("min")) {
                builder.minPrice(price);
            } else if (message.contains("từ") && message.contains("đến")) {
                // Range detected
                builder.minPrice(price);
                if (matcher.find()) {
                    String number2 = matcher.group(1);
                    String unit2 = matcher.group(2).toLowerCase();
                    BigDecimal maxPrice = new BigDecimal(number2);
                    if (unit2.equals("k") || unit2.contains("nghìn") || unit2.contains("ngàn")) {
                        maxPrice = maxPrice.multiply(new BigDecimal("1000"));
                    } else if (unit2.contains("triệu") || unit2.equals("tr")) {
                        maxPrice = maxPrice.multiply(new BigDecimal("1000000"));
                    }
                    builder.maxPrice(maxPrice);
                }
                break;
            }
        }
    }
    
    private void extractQuantity(String message, IntentResult.IntentResultBuilder builder) {
        // Extract quantity: "5 khóa học", "top 10", "3 course"
        Pattern qtyPattern = Pattern.compile("(\\d+)\\s*(khóa|course|top)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = qtyPattern.matcher(message);
        if (matcher.find()) {
            builder.quantity(Integer.parseInt(matcher.group(1)));
        }
    }
    
    private void extractTargetAudience(String message, IntentResult.IntentResultBuilder builder) {
        if (containsAny(message, "beginner", "cơ bản", "mới bắt đầu", "newbie", "người mới")) {
            builder.targetAudience("beginner");
        } else if (containsAny(message, "intermediate", "trung cấp", "nâng cao")) {
            builder.targetAudience("intermediate");
        } else if (containsAny(message, "advanced", "chuyên sâu", "expert", "pro")) {
            builder.targetAudience("advanced");
        }
    }
    
    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
