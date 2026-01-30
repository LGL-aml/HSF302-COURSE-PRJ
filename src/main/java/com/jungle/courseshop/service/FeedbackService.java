package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    List<FeedbackResponse> getFeedbackTreeForVideo(Long videoId);

    FeedbackResponse createComment(Long videoId, String content);

    FeedbackResponse reply(Long parentId, String content);
}

