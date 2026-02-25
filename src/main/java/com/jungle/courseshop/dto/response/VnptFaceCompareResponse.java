package com.jungle.courseshop.dto.response;

import lombok.Data;

@Data
public class VnptFaceCompareResponse {
    private Integer statusCode;
    private String message;
    private FaceCompareObject object;

    @Data
    public static class FaceCompareObject {
        private String result;
        private String msg;
        private Double prob;
    }
}
