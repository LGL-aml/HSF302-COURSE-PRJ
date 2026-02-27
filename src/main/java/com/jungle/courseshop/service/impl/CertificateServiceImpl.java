package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.service.CertificateService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final SpringTemplateEngine templateEngine;

    public byte[] generateCertificatePdf(String studentName, String courseName) throws Exception {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("courseName", courseName);
        context.setVariable("issueDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // 1. Đọc file ảnh background từ thư mục resources và chuyển thành Base64
        ClassPathResource imgFile = new ClassPathResource("static/images/certificate.png");
        byte[] imgBytes = Files.readAllBytes(imgFile.getFile().toPath());
        String base64Image = Base64.getEncoder().encodeToString(imgBytes);

        // Truyền chuỗi Base64 sang Thymeleaf
        context.setVariable("bgImage", "data:image/jpeg;base64," + base64Image);

        // 2. Render HTML
        String htmlContent = templateEngine.process("certificate/certificate", context);

        // 3. Tạo PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            // BẮT BUỘC: Add font tiếng Việt để không bị lỗi chữ ô vuông
            File fontFile = new ClassPathResource("fonts/ARIAL.TTF").getFile();
            builder.useFont(fontFile, "Arial");

            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        }
    }

}
