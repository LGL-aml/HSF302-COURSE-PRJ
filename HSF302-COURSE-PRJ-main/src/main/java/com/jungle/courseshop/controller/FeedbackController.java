package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.request.FeedbackCreateRequest;
import com.jungle.courseshop.dto.response.FeedbackResponse;
import com.jungle.courseshop.service.FeedbackService;
import com.jungle.courseshop.service.impl.FeedbackServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/videos/{videoId}")
    public List<FeedbackResponse> list(@PathVariable Long videoId) {
        return feedbackService.getFeedbackTreeForVideo(videoId);
    }

    @PostMapping("/videos/{videoId}")
    public FeedbackResponse create(@PathVariable Long videoId, @RequestBody FeedbackCreateRequest request) {
        return feedbackService.createComment(videoId, request.getContent());
    }

    @PostMapping("/{commentId}/reply")
    public FeedbackResponse reply(@PathVariable Long commentId, @RequestBody FeedbackCreateRequest request) {
        return feedbackService.reply(commentId, request.getContent());
    }
}
