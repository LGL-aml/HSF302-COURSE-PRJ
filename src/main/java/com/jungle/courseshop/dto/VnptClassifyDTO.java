package com.jungle.courseshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VnptClassifyDTO {
    @JsonProperty("name")
    private String name; // Ví dụ: "new_front"

    @JsonProperty("type")
    private Integer type;
}
