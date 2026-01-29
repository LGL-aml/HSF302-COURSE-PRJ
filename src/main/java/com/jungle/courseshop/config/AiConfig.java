package com.jungle.courseshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiConfig {

    @Bean
    public WebClient cerebrasWebClient(
            @Value("${spring.ai.openai.base-url:https://api.cerebras.ai/v1}") String baseUrl,
            @Value("${spring.ai.openai.api-key:}") String apiKey
    ) {
        WebClient.Builder builder = WebClient.builder().baseUrl(baseUrl);
        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + apiKey);
        }
        builder.defaultHeader("Content-Type", "application/json");
        return builder.build();
    }
}
