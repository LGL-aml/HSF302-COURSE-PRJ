package com.jungle.courseshop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jungle.courseshop.dto.UploadDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VnptUploadResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("object")
    private UploadDTO object;

    public static class UploadObject extends UploadDTO {
        public UploadObject(String hash123) {
        }
    }
}
