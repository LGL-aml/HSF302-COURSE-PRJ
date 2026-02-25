package com.jungle.courseshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VnptFaceCompareRequest {
    private String img_front;      // Hash ảnh mặt trước CCCD (đã lấy từ upload)
    private String img_face;       // Hash ảnh chân dung chụp trực tiếp
    private String client_session;
    private String token;
}
