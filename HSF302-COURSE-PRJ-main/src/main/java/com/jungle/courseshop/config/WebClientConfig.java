package com.jungle.courseshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${VNPT_EKYC_API_URL}")
    private String apiUrl;

    @Value("${VNPT_EKYC_TOKEN_ID}")
    private String tokenId;

    @Value("${VNPT_EKYC_TOKEN_KEY}")
    private String tokenKey;

    @Value("${VNPT_EKYC_ACCESS_TOKEN}")
    private String accessToken;

    @Bean
    public WebClient vnptWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(apiUrl)
                .defaultHeader("Token-id", tokenId)
                .defaultHeader("Token-key", tokenKey)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }
}
