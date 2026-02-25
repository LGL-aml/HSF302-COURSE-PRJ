package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String userName) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("userName", userName);

        String htmlContent = templateEngine.process("email/welcome-email", context); // "welcome-email.html" trong templates

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom(fromEmail, "DUPSS Support");
        helper.setTo(toEmail);
        helper.setSubject("Chào mừng bạn đến với DUPSS");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        try {
            log.info("Bắt đầu gửi email đến: {}, với tiêu đề: {}", to, subject);

            if (to == null || to.trim().isEmpty()) {
                throw new IllegalArgumentException("Địa chỉ email người nhận không được để trống");
            }

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Nội dung email không được để trống");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "DUPSS Support");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email đã được gửi thành công đến: {}", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Lỗi khi gửi email đến {}: {}", to, e.getMessage());
            log.error("Chi tiết lỗi:", e);
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi gửi email đến {}: {}", to, e.getMessage());
            log.error("Chi tiết lỗi:", e);
            throw new MessagingException("Không thể gửi email: " + e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendEnrollmentSuccessEmail(String toEmail, String userName,
                                           String courseTitle, int duration,
                                           String instructor, String enrollDate) throws MessagingException, UnsupportedEncodingException {

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("courseTitle", courseTitle);
        context.setVariable("duration", duration);
        context.setVariable("instructor", instructor);
        context.setVariable("enrollDate", enrollDate);

        String htmlContent = templateEngine.process("email/course-enroll-success", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom(fromEmail, "DUPSS Support");
        helper.setTo(toEmail);
        helper.setSubject("Đăng ký khóa học thành công");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    @Async
    public void sendCourseCompletionEmail(String toEmail,
                                          String userName,
                                          String courseTitle,
                                          int duration,
                                          String instructor,
                                          String completedDate) throws MessagingException, UnsupportedEncodingException {

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("courseTitle", courseTitle);
        context.setVariable("duration", duration);
        context.setVariable("instructor", instructor);
        context.setVariable("completedDate", completedDate);

        String htmlContent = templateEngine.process("email/course-completed", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom(fromEmail, "DUPSS Support");
        helper.setTo(toEmail);
        helper.setSubject("🎓 Bạn đã hoàn thành khóa học thành công!");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
