package com.jungle.courseshop.service.rag;

import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.repository.CourseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for managing course embeddings in Vector Store
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEmbeddingService {

    private final VectorStore vectorStore;
    private final CourseRepo courseRepo;

    /**
     * Generate and store embedding for a course
     */
    @Transactional
    public void embedCourse(Course course) {
        log.info("Generating embedding for course: {}", course.getTitle());

        Document document = new Document(
            toEmbeddingText(course),
            Map.of(
                "courseId", course.getId().toString(),
                "title", course.getTitle(),
                "price", course.getPrice() != null ? course.getPrice().toString() : "0",
                "topic", course.getTopic() != null ? course.getTopic().getName() : "",
                "creator", course.getCreator() != null ? course.getCreator().getFullname() : "",
                "enrolledCount", course.getEnrolledCount() != null ? course.getEnrolledCount().toString() : "0"
            )
        );

        vectorStore.add(List.of(document));
        log.info("Embedding stored for course ID: {}", course.getId());
    }

    /**
     * Generate embeddings for all active courses
     */
    @Transactional
    public void embedAllCourses() {
        log.info("Generating embeddings for all courses...");
        List<Course> courses = courseRepo.findByActiveTrue();

        List<Document> documents = courses.stream()
            .map(course -> new Document(
                toEmbeddingText(course),
                Map.of(
                    "courseId", course.getId().toString(),
                    "title", course.getTitle(),
                    "price", course.getPrice() != null ? course.getPrice().toString() : "0",
                    "topic", course.getTopic() != null ? course.getTopic().getName() : "",
                    "creator", course.getCreator() != null ? course.getCreator().getFullname() : "",
                    "enrolledCount", course.getEnrolledCount() != null ? course.getEnrolledCount().toString() : "0"
                )
            ))
            .toList();

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
            log.info("Generated embeddings for {} courses", courses.size());
        }
    }

    /**
     * Search similar courses using semantic search
     */
    public List<Document> searchSimilar(String query, int topK) {
        log.info("Searching for similar courses: {}", query);
        return vectorStore.similaritySearch(query);
    }

    /**
     * Generate text representation for embedding
     */
    private String toEmbeddingText(Course course) {
        return String.format("""
            Tên khóa học: %s
            Mô tả: %s
            Nội dung: %s
            Giá: %s VNĐ
            Chủ đề: %s
            Giảng viên: %s
            Thời lượng: %s phút
            Số học viên: %s
            """,
            course.getTitle(),
            course.getDescription() != null ? course.getDescription() : "",
            course.getContent() != null ? course.getContent() : "",
            course.getPrice() != null ? course.getPrice() : "0",
            course.getTopic() != null ? course.getTopic().getName() : "Chưa phân loại",
            course.getCreator() != null ? course.getCreator().getFullname() : "Chưa xác định",
            course.getDuration() != null ? course.getDuration() : "Chưa xác định",
            course.getEnrolledCount() != null ? course.getEnrolledCount() : "0"
        );
    }
}
