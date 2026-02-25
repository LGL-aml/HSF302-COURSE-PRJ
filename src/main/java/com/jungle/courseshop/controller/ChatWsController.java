package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.ChatMessageDto;
import com.jungle.courseshop.dto.ChatSendRequest;
import com.jungle.courseshop.entity.ChatMessage;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.repository.ChatMessageRepo;
import com.jungle.courseshop.service.ChatAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final ChatAuthorizationService chatAuthorizationService;

    @MessageMapping("/chat.send")
    public void send(@Payload ChatSendRequest inbound, @AuthenticationPrincipal User principal) {
        if (principal == null) {
            return;
        }

        if (inbound.getCourseId() == null || inbound.getRecipientId() == null) {
            return;
        }

        if (inbound.getContent() == null || inbound.getContent().isBlank()) {
            return;
        }

        User sender = userRepo.findById(principal.getId()).orElse(null);
        User recipient = userRepo.findById(inbound.getRecipientId()).orElse(null);
        Course course = courseRepo.findById(inbound.getCourseId()).orElse(null);
        if (sender == null || recipient == null || course == null) {
            return;
        }

        if (!chatAuthorizationService.canChat(sender, recipient, course)) {
            return;
        }

        ChatMessage msg = new ChatMessage();
        msg.setSender(sender);
        msg.setRecipient(recipient);
        msg.setContent(inbound.getContent().trim());
        ChatMessage saved = chatMessageRepo.save(msg);

        ChatMessageDto outbound = new ChatMessageDto();
        outbound.setSenderId(sender.getId());
        outbound.setSenderName(sender.getFullname() != null ? sender.getFullname() : sender.getUsername());
        outbound.setSenderAvatar(sender.getAvatar());
        outbound.setRecipientId(recipient.getId());
        outbound.setRecipientName(recipient.getFullname() != null ? recipient.getFullname() : recipient.getUsername());
        outbound.setContent(saved.getContent());
        outbound.setSentAt(saved.getSentAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        messagingTemplate.convertAndSendToUser(String.valueOf(recipient.getId()), "/queue/messages", outbound);
        messagingTemplate.convertAndSendToUser(String.valueOf(sender.getId()), "/queue/messages", outbound);
    }
}
