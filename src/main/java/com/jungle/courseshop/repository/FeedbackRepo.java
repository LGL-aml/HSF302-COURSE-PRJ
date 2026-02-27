package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.CourseVideo;
import com.jungle.courseshop.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    List<Feedback> findByVideo_IdAndParentIsNullOrderByCreatedAtAsc(Long videoId);

    @Modifying
    @Query("DELETE FROM Feedback f WHERE f.parent IS NOT NULL AND f.video IN :videos")
    void deleteRepliesByVideoIn(@Param("videos") List<CourseVideo> videos);

    @Modifying
    @Query("DELETE FROM Feedback f WHERE f.video IN :videos")
    void deleteByVideoIn(@Param("videos") List<CourseVideo> videos);
}
