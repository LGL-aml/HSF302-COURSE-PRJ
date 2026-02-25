package com.jungle.courseshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_question")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, length = 2000)
    private String question;

    // Loại câu hỏi: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, CODE
    @Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.MULTIPLE_CHOICE;

    // Điểm của câu hỏi này
    private Integer points = 1;

    // Thứ tự câu hỏi trong quiz
    private Integer orderIndex;

    // Giải thích đáp án
    @Column(length = 2000)
    private String explanation;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> answers = new ArrayList<>();

    public enum QuestionType {
        MULTIPLE_CHOICE,  // Trắc nghiệm 1 đáp án
        MULTIPLE_SELECT,  // Trắc nghiệm nhiều đáp án
        TRUE_FALSE,       // Đúng/Sai
        SHORT_ANSWER,     // Câu trả lời ngắn
        CODE              // Code exercise
    }
}
