package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    List<Feedback> findByVideo_IdAndParentIsNullOrderByCreatedAtAsc(Long videoId);
}
