package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.request.QuizSubmitRequest;
import com.jungle.courseshop.dto.response.QuizResponse;
import com.jungle.courseshop.dto.response.QuizResultResponse;

public interface QuizService {
    /**
     * Lấy quiz theo module ID (cho sinh viên làm bài - ẩn đáp án)
     */
    QuizResponse getQuizForStudent(Long moduleId);

    /**
     * Lấy quiz theo module ID (cho giảng viên xem - hiện đáp án)
     */
    QuizResponse getQuizForLecturer(Long moduleId);

    /**
     * Nộp bài quiz và trả kết quả
     */
    QuizResultResponse submitQuiz(QuizSubmitRequest request);

    /**
     * Kiểm tra sinh viên đã pass quiz của module chưa
     */
    boolean hasPassedQuiz(Long moduleId);
}
