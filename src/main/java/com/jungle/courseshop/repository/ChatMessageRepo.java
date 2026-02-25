package com.jungle.courseshop.repository;

import com.jungle.courseshop.entity.ChatMessage;
import com.jungle.courseshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50BySenderAndRecipientOrSenderAndRecipientOrderBySentAtAsc(
            User sender1, User recipient1,
            User sender2, User recipient2
    );
}
