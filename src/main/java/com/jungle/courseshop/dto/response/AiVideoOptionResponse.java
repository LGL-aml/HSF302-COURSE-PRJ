package com.jungle.courseshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiVideoOptionResponse {
    private Long id;
    private String title;
    private String videoUrl;
}
