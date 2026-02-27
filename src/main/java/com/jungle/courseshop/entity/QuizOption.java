package com.jungle.courseshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class QuizOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String optionText;

    @Column(nullable = false)
    private Boolean isCorrect = false;

    /**
     * Giải thích tại sao đáp án đúng/sai (hiện sau khi nộp bài)
     */
    @Column(length = 2000)
    private String explanation;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;
}
