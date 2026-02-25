package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.request.QuizCreateRequest;
import com.jungle.courseshop.dto.request.QuizSubmitRequest;
import com.jungle.courseshop.dto.response.QuizAttemptResponse;
import com.jungle.courseshop.dto.response.QuizDetailResponse;
import com.jungle.courseshop.dto.response.QuizResponse;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.repository.*;
import com.jungle.courseshop.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepo quizRepo;
    private final QuizQuestionRepo questionRepo;
    private final QuizAnswerRepo answerRepo;
    private final QuizAttemptRepo attemptRepo;
    private final QuizAttemptAnswerRepo attemptAnswerRepo;
    private final CourseRepo courseRepo;
    private final CourseModuleRepo moduleRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public QuizResponse createQuiz(QuizCreateRequest request) {
        User currentUser = getCurrentUser();
        
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .passingScore(request.getPassingScore())
                .maxAttempts(request.getMaxAttempts())
                .active(true)
                .build();

        if (request.getCourseId() != null) {
            Course course = courseRepo.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            quiz.setCourse(course);
        }

        if (request.getModuleId() != null) {
            CourseModule module = moduleRepo.findById(request.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found"));
            quiz.setModule(module);
        }

        quiz = quizRepo.save(quiz);

        // Create questions and answers
        if (request.getQuestions() != null) {
            int orderIndex = 0;
            for (QuizCreateRequest.QuestionRequest qReq : request.getQuestions()) {
                QuizQuestion question = QuizQuestion.builder()
                        .quiz(quiz)
                        .question(qReq.getQuestion())
                        .type(QuizQuestion.QuestionType.valueOf(qReq.getType()))
                        .points(qReq.getPoints() != null ? qReq.getPoints() : 1)
                        .explanation(qReq.getExplanation())
                        .orderIndex(orderIndex++)
                        .build();
                question = questionRepo.save(question);

                if (qReq.getAnswers() != null) {
                    int answerOrder = 0;
                    for (QuizCreateRequest.AnswerRequest aReq : qReq.getAnswers()) {
                        QuizAnswer answer = QuizAnswer.builder()
                                .question(question)
                                .answerText(aReq.getAnswerText())
                                .isCorrect(aReq.getIsCorrect())
                                .orderIndex(answerOrder++)
                                .build();
                        answerRepo.save(answer);
                    }
                }
            }
        }

        return mapToQuizResponse(quiz, currentUser.getId());
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long id, QuizCreateRequest request) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setDuration(request.getDuration());
        quiz.setPassingScore(request.getPassingScore());
        quiz.setMaxAttempts(request.getMaxAttempts());

        // Delete old questions and answers
        List<QuizQuestion> oldQuestions = questionRepo.findByQuizIdOrderByOrderIndexAsc(id);
        for (QuizQuestion q : oldQuestions) {
            answerRepo.deleteByQuestionId(q.getId());
        }
        questionRepo.deleteByQuizId(id);

        // Create new questions
        if (request.getQuestions() != null) {
            int orderIndex = 0;
            for (QuizCreateRequest.QuestionRequest qReq : request.getQuestions()) {
                QuizQuestion question = QuizQuestion.builder()
                        .quiz(quiz)
                        .question(qReq.getQuestion())
                        .type(QuizQuestion.QuestionType.valueOf(qReq.getType()))
                        .points(qReq.getPoints() != null ? qReq.getPoints() : 1)
                        .explanation(qReq.getExplanation())
                        .orderIndex(orderIndex++)
                        .build();
                question = questionRepo.save(question);

                if (qReq.getAnswers() != null) {
                    int answerOrder = 0;
                    for (QuizCreateRequest.AnswerRequest aReq : qReq.getAnswers()) {
                        QuizAnswer answer = QuizAnswer.builder()
                                .question(question)
                                .answerText(aReq.getAnswerText())
                                .isCorrect(aReq.getIsCorrect())
                                .orderIndex(answerOrder++)
                                .build();
                        answerRepo.save(answer);
                    }
                }
            }
        }

        quiz = quizRepo.save(quiz);
        return mapToQuizResponse(quiz, getCurrentUser().getId());
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setActive(false);
        quizRepo.save(quiz);
    }

    @Override
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return mapToQuizResponse(quiz, getCurrentUser().getId());
    }

    @Override
    public QuizDetailResponse getQuizDetail(Long id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuizQuestion> questions = questionRepo.findByQuizIdOrderByOrderIndexAsc(id);
        
        int totalPoints = questions.stream().mapToInt(QuizQuestion::getPoints).sum();

        List<QuizDetailResponse.QuestionDetail> questionDetails = questions.stream()
                .map(q -> {
                    List<QuizAnswer> answers = answerRepo.findByQuestionIdOrderByOrderIndexAsc(q.getId());
                    List<QuizDetailResponse.AnswerDetail> answerDetails = answers.stream()
                            .map(a -> QuizDetailResponse.AnswerDetail.builder()
                                    .id(a.getId())
                                    .answerText(a.getAnswerText())
                                    .build())
                            .collect(Collectors.toList());

                    return QuizDetailResponse.QuestionDetail.builder()
                            .id(q.getId())
                            .question(q.getQuestion())
                            .type(q.getType().name())
                            .points(q.getPoints())
                            .answers(answerDetails)
                            .build();
                })
                .collect(Collectors.toList());

        return QuizDetailResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .totalPoints(totalPoints)
                .questions(questionDetails)
                .build();
    }

    @Override
    public List<QuizResponse> getQuizzesByCourse(Long courseId) {
        Long userId = getCurrentUser().getId();
        List<Quiz> quizzes = quizRepo.findByCourseIdAndActiveTrue(courseId);
        return quizzes.stream()
                .map(q -> mapToQuizResponse(q, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResponse> getQuizzesByModule(Long moduleId) {
        Long userId = getCurrentUser().getId();
        List<Quiz> quizzes = quizRepo.findByModuleIdAndActiveTrue(moduleId);
        return quizzes.stream()
                .map(q -> mapToQuizResponse(q, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResponse> getMyQuizzes() {
        // For lecturers - get quizzes for their courses
        User currentUser = getCurrentUser();
        List<Course> courses = courseRepo.findByCreatorAndActiveTrue(currentUser);
        
        List<Quiz> quizzes = new ArrayList<>();
        for (Course course : courses) {
            quizzes.addAll(quizRepo.findByCourseId(course.getId()));
        }
        
        return quizzes.stream()
                .map(q -> mapToQuizResponse(q, currentUser.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizAttemptResponse startQuizAttempt(Long quizId) {
        User user = getCurrentUser();
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Check max attempts
        Long attemptCount = attemptRepo.countByQuizIdAndUserId(quizId, user.getId());
        if (quiz.getMaxAttempts() != null && attemptCount >= quiz.getMaxAttempts()) {
            throw new RuntimeException("Maximum attempts reached");
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .user(user)
                .attemptNumber(attemptCount.intValue() + 1)
                .startedAt(LocalDateTime.now())
                .build();

        attempt = attemptRepo.save(attempt);

        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .attemptNumber(attempt.getAttemptNumber())
                .startedAt(attempt.getStartedAt())
                .build();
    }

    @Override
    @Transactional
    public QuizAttemptResponse submitQuiz(QuizSubmitRequest request) {
        QuizAttempt attempt = attemptRepo.findById(request.getAttemptId())
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        Quiz quiz = attempt.getQuiz();
        List<QuizQuestion> questions = questionRepo.findByQuizIdOrderByOrderIndexAsc(quiz.getId());

        int totalPoints = 0;
        int earnedPoints = 0;

        // Process each answer
        for (QuizSubmitRequest.AnswerSubmit answerSubmit : request.getAnswers()) {
            QuizQuestion question = questions.stream()
                    .filter(q -> q.getId().equals(answerSubmit.getQuestionId()))
                    .findFirst()
                    .orElse(null);

            if (question == null) continue;

            totalPoints += question.getPoints();

            List<QuizAnswer> correctAnswers = answerRepo.findByQuestionIdAndIsCorrectTrue(question.getId());
            String correctAnswerIds = correctAnswers.stream()
                    .map(a -> a.getId().toString())
                    .collect(Collectors.joining(","));

            boolean isCorrect = checkAnswer(question, answerSubmit, correctAnswers);
            int pointsEarned = isCorrect ? question.getPoints() : 0;
            earnedPoints += pointsEarned;

            QuizAttemptAnswer attemptAnswer = QuizAttemptAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .selectedAnswerIds(answerSubmit.getSelectedAnswerIds())
                    .textAnswer(answerSubmit.getTextAnswer())
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .build();

            attemptAnswerRepo.save(attemptAnswer);
        }

        // Calculate results
        double percentage = totalPoints > 0 ? (earnedPoints * 100.0 / totalPoints) : 0;
        boolean passed = quiz.getPassingScore() != null && percentage >= quiz.getPassingScore();

        attempt.setScore((double) earnedPoints);
        attempt.setTotalPoints(totalPoints);
        attempt.setPercentage(percentage);
        attempt.setPassed(passed);
        attempt.setSubmittedAt(LocalDateTime.now());

        if (attempt.getStartedAt() != null) {
            long seconds = java.time.Duration.between(attempt.getStartedAt(), LocalDateTime.now()).getSeconds();
            attempt.setTimeSpent((int) seconds);
        }

        attemptRepo.save(attempt);

        return getAttemptDetail(attempt.getId());
    }

    @Override
    public QuizAttemptResponse getAttemptDetail(Long attemptId) {
        QuizAttempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<QuizAttemptAnswer> attemptAnswers = attemptAnswerRepo.findByAttemptId(attemptId);
        
        List<QuizAttemptResponse.AnswerResult> answerResults = attemptAnswers.stream()
                .map(aa -> {
                    QuizQuestion question = aa.getQuestion();
                    List<QuizAnswer> allAnswers = answerRepo.findByQuestionIdOrderByOrderIndexAsc(question.getId());
                    
                    List<String> selectedIds = aa.getSelectedAnswerIds() != null 
                            ? Arrays.asList(aa.getSelectedAnswerIds().split(","))
                            : new ArrayList<>();

                    List<QuizAttemptResponse.AnswerOption> options = allAnswers.stream()
                            .map(ans -> QuizAttemptResponse.AnswerOption.builder()
                                    .id(ans.getId())
                                    .text(ans.getAnswerText())
                                    .isCorrect(ans.getIsCorrect())
                                    .wasSelected(selectedIds.contains(ans.getId().toString()))
                                    .build())
                            .collect(Collectors.toList());

                    String correctAnswerIds = allAnswers.stream()
                            .filter(QuizAnswer::getIsCorrect)
                            .map(a -> a.getId().toString())
                            .collect(Collectors.joining(","));

                    return QuizAttemptResponse.AnswerResult.builder()
                            .questionId(question.getId())
                            .question(question.getQuestion())
                            .questionType(question.getType().name())
                            .points(question.getPoints())
                            .selectedAnswerIds(aa.getSelectedAnswerIds())
                            .textAnswer(aa.getTextAnswer())
                            .isCorrect(aa.getIsCorrect())
                            .pointsEarned(aa.getPointsEarned())
                            .correctAnswerIds(correctAnswerIds)
                            .explanation(question.getExplanation())
                            .options(options)
                            .build();
                })
                .collect(Collectors.toList());

        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .attemptNumber(attempt.getAttemptNumber())
                .score(attempt.getScore())
                .totalPoints(attempt.getTotalPoints())
                .percentage(attempt.getPercentage())
                .passed(attempt.getPassed())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .timeSpent(attempt.getTimeSpent())
                .answers(answerResults)
                .build();
    }

    @Override
    public List<QuizAttemptResponse> getMyAttempts(Long quizId) {
        User user = getCurrentUser();
        List<QuizAttempt> attempts = attemptRepo.findByQuizIdAndUserIdOrderByAttemptNumberDesc(quizId, user.getId());
        return attempts.stream()
                .map(a -> QuizAttemptResponse.builder()
                        .id(a.getId())
                        .quizId(a.getQuiz().getId())
                        .quizTitle(a.getQuiz().getTitle())
                        .attemptNumber(a.getAttemptNumber())
                        .score(a.getScore())
                        .totalPoints(a.getTotalPoints())
                        .percentage(a.getPercentage())
                        .passed(a.getPassed())
                        .startedAt(a.getStartedAt())
                        .submittedAt(a.getSubmittedAt())
                        .timeSpent(a.getTimeSpent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizAttemptResponse> getAllAttemptsForQuiz(Long quizId) {
        List<QuizAttempt> attempts = attemptRepo.findByQuizIdOrderByStartedAtDesc(quizId);
        return attempts.stream()
                .map(a -> QuizAttemptResponse.builder()
                        .id(a.getId())
                        .quizId(a.getQuiz().getId())
                        .quizTitle(a.getQuiz().getTitle())
                        .attemptNumber(a.getAttemptNumber())
                        .score(a.getScore())
                        .totalPoints(a.getTotalPoints())
                        .percentage(a.getPercentage())
                        .passed(a.getPassed())
                        .startedAt(a.getStartedAt())
                        .submittedAt(a.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserPassedQuiz(Long quizId, Long userId) {
        return attemptRepo.existsByQuizIdAndUserIdAndPassedTrue(quizId, userId);
    }

    @Override
    public Double getUserBestScore(Long quizId, Long userId) {
        return attemptRepo.findMaxScoreByQuizIdAndUserId(quizId, userId);
    }

    @Override
    public Integer getUserAttemptCount(Long quizId, Long userId) {
        return attemptRepo.countByQuizIdAndUserId(quizId, userId).intValue();
    }

    // Helper methods
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private QuizResponse mapToQuizResponse(Quiz quiz, Long userId) {
        Long questionCount = questionRepo.countByQuizId(quiz.getId());
        Integer attemptCount = getUserAttemptCount(quiz.getId(), userId);
        Double bestScore = getUserBestScore(quiz.getId(), userId);
        Boolean passed = hasUserPassedQuiz(quiz.getId(), userId);

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .courseId(quiz.getCourse() != null ? quiz.getCourse().getId() : null)
                .courseTitle(quiz.getCourse() != null ? quiz.getCourse().getTitle() : null)
                .moduleId(quiz.getModule() != null ? quiz.getModule().getId() : null)
                .moduleTitle(quiz.getModule() != null ? quiz.getModule().getTitle() : null)
                .duration(quiz.getDuration())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .questionCount(questionCount.intValue())
                .active(quiz.getActive())
                .createdAt(quiz.getCreatedAt())
                .userAttemptCount(attemptCount)
                .userBestScore(bestScore)
                .userPassed(passed)
                .build();
    }

    private boolean checkAnswer(QuizQuestion question, QuizSubmitRequest.AnswerSubmit answerSubmit, 
                                List<QuizAnswer> correctAnswers) {
        if (question.getType() == QuizQuestion.QuestionType.MULTIPLE_CHOICE 
                || question.getType() == QuizQuestion.QuestionType.MULTIPLE_SELECT) {
            String correctIds = correctAnswers.stream()
                    .map(a -> a.getId().toString())
                    .sorted()
                    .collect(Collectors.joining(","));
            
            String submittedIds = answerSubmit.getSelectedAnswerIds() != null 
                    ? Arrays.stream(answerSubmit.getSelectedAnswerIds().split(","))
                            .sorted()
                            .collect(Collectors.joining(","))
                    : "";
            
            return correctIds.equals(submittedIds);
        }
        
        // For text/code questions, simple comparison (can be enhanced with more logic)
        if (answerSubmit.getTextAnswer() != null && !correctAnswers.isEmpty()) {
            return answerSubmit.getTextAnswer().trim()
                    .equalsIgnoreCase(correctAnswers.get(0).getAnswerText().trim());
        }
        
        return false;
    }
}
