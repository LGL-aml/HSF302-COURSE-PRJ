package com.jungle.courseshop.repository.rag;

import com.jungle.courseshop.entity.rag.RagChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for RAG Chat Session
 */
@Repository
public interface RagChatSessionRepository extends JpaRepository<RagChatSession, Long> {
}
