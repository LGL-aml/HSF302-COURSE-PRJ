package com.jungle.courseshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_attempt")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Lần thứ mấy làm quiz này (1, 2, 3...)
    private Integer attemptNumber;

    // Điểm số đạt được
    private Double score;

    // Tổng điểm có thể đạt
    private Integer totalPoints;

    // Phần trăm đạt được
    private Double percentage;

    // Đã pass hay chưa
    private Boolean passed = false;

    // Thời gian bắt đầu
    private LocalDateTime startedAt;

    // Thời gian nộp bài
    private LocalDateTime submittedAt;

    // Thời gian làm bài (giây)
    private Integer timeSpent;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttemptAnswer> answers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }
}
