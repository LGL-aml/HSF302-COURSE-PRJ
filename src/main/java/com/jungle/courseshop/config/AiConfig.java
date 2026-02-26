package com.jungle.courseshop.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring AI Configuration with RAG support
 */
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

    /**
     * Vector Store Bean - Using SimpleVectorStore (In-Memory) for MySQL
     * Note: SimpleVectorStore is suitable for small to medium datasets
     * For production with large data, consider using a dedicated vector database
     */
    @Bean
    @Primary
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * Embedding Model Bean - Using Transformers (Local ONNX Model)
     * ChatClient.Builder is auto-configured by Spring AI
     */
}
