package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public void notifyUser(String username, NotificationMessage message) {
//        messagingTemplate.convertAndSendToUser(
//                username,
//                "/queue/notifications",
//                message
//        );
//    }
//
//    public void notifyAdmin(NotificationMessage message) {
//        messagingTemplate.convertAndSend(
//                "/topic/admin-orders",
//                message
//        );
//    }
}
