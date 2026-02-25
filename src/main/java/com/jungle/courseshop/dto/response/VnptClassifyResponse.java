package com.jungle.courseshop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jungle.courseshop.dto.VnptClassifyDTO;
import lombok.Data;


@Data
public class VnptClassifyResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private int statusCode;

    // Trường "object" chứa thông tin type
    @JsonProperty("object")
    private VnptClassifyDTO object;

    public static class ClassifyObject extends VnptClassifyDTO {
        public ClassifyObject(int i) {
        }
    }
}
