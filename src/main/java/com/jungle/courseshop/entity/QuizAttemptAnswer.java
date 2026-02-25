package com.jungle.courseshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_attempt_answer")
public class QuizAttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    // Đáp án user chọn (có thể nhiều đáp án, ngăn cách bằng dấu phẩy: "1,3,4")
    @Column(length = 500)
    private String selectedAnswerIds;

    // Nếu là câu hỏi text/code, lưu câu trả lời
    @Column(length = 5000)
    private String textAnswer;

    // Câu trả lời này đúng hay sai
    private Boolean isCorrect = false;

    // Điểm đạt được cho câu này
    private Integer pointsEarned = 0;
}
