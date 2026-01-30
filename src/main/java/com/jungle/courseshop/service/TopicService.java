package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.request.TopicRequest;
import com.jungle.courseshop.dto.response.TopicResponse;
import com.jungle.courseshop.entity.Topic;

import java.util.List;

public interface TopicService {

    List<Topic> getAll();

    TopicResponse getTopicById(Long id);

    TopicResponse create(TopicRequest request);

    List<TopicResponse> getTopicsCreatedByCurrentUser();

    List<TopicResponse> getAllTopics();

    TopicResponse update(Long id, TopicRequest request);

    void delete(Long id);
}

