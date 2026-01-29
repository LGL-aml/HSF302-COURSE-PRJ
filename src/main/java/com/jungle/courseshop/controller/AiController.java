package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.AiChatRequest;
import com.jungle.courseshop.dto.request.AiQuizGenerateRequest;
import com.jungle.courseshop.dto.response.AiChatResponse;
import com.jungle.courseshop.dto.response.AiCourseOptionResponse;
import com.jungle.courseshop.dto.response.AiQuizResponse;
import com.jungle.courseshop.dto.response.AiVideoOptionResponse;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.CourseVideoRepo;
import com.jungle.courseshop.service.CerebrasAiService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AiController {

    private final CerebrasAiService cerebrasAiService;
    private final CourseRepo courseRepo;
    private final CourseVideoRepo courseVideoRepo;

    @PostMapping("/chat")
    public AiChatResponse chat(@RequestBody AiChatRequest request) {
        return cerebrasAiService.chat(request);
    }

    @GetMapping("/courses")
    public List<AiCourseOptionResponse> listCourses() {
        return courseRepo.findByActiveTrue()
                .stream()
                .map(c -> new AiCourseOptionResponse(c.getId(), c.getTitle()))
                .toList();
    }

    @GetMapping("/courses/{courseId}/videos")
    public List<AiVideoOptionResponse> listVideos(@PathVariable Long courseId) {
        return courseVideoRepo.findByCourseModule_Course_Id(courseId)
                .stream()
                .map(v -> new AiVideoOptionResponse(v.getId(), v.getTitle(), v.getVideoUrl()))
                .toList();
    }

    @PostMapping("/quiz/generate")
    public AiQuizResponse generateQuiz(@RequestBody AiQuizGenerateRequest request) {
        return cerebrasAiService.generateQuiz(request);
    }
}
