package com.jungle.courseshop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jungle.courseshop.dto.VnptOcrDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VnptOcrFullResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("object")
    private VnptOcrDTO object;
}
