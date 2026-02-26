package com.jungle.courseshop.repository.rag;

import com.jungle.courseshop.entity.rag.RagChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for RAG Chat Message
 */
@Repository
public interface RagChatMessageRepository extends JpaRepository<RagChatMessage, Long> {

    List<RagChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
