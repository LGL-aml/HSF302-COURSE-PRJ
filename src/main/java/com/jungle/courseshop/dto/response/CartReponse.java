package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartReponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CartItemResponse {
        private Long courseId;
        private String courseTitle;
        private String courseThumbnail;
        private String lecturerName;
        private BigDecimal price;
    }
}
