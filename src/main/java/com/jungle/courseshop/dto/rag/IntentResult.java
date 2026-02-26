package com.jungle.courseshop.dto.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Intent Classification Result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentResult {

    private IntentType intent;
    private String rawQuery;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private String targetAudience; // beginner, intermediate, advanced
    private Integer quantity;
    private String keyword;

    public enum IntentType {
        COURSE_SEARCH,      // Tìm kiếm khóa học
        COURSE_RECOMMEND,   // Gợi ý khóa học
        PRICING_INFO,       // Hỏi về giá
        DISCOUNT_POLICY,    // Hỏi về giảm giá
        ENROLLMENT_INFO,    // Hỏi về đặng ký
        PLATFORM_INFO,      // Hỏi về nền tảng
        GENERAL_CHAT        // Chat chung
    }
}
