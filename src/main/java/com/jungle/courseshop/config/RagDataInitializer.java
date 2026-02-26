package com.jungle.courseshop.config;

import com.jungle.courseshop.service.rag.CourseEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Data Initializer - Generate embeddings for all courses on startup
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RagDataInitializer {

    private final CourseEmbeddingService courseEmbeddingService;

    @Bean
    public CommandLineRunner initRagData() {
        return args -> {
            try {
                log.info("=== Starting RAG Data Initialization ===");
                
                // Generate embeddings for all active courses
                courseEmbeddingService.embedAllCourses();
                
                log.info("=== RAG Data Initialization Completed Successfully ===");
            } catch (Exception e) {
                log.error("Error during RAG data initialization", e);
                // Don't fail the application startup
            }
        };
    }
}
