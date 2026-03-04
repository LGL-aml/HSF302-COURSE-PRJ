package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.service.CertificateService;
import com.jungle.courseshop.service.CloudinaryService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateServiceImpl implements CertificateService {

    private final SpringTemplateEngine templateEngine;
    private final CloudinaryService cloudinaryService;

    @Override
    public byte[] generateCertificatePdf(String studentName, String courseName) throws Exception {
        Context context = buildContext(studentName, courseName);
        String htmlContent = templateEngine.process("certificate/certificate", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            File fontFile = new ClassPathResource("fonts/ARIAL.TTF").getFile();
            builder.useFont(fontFile, "Arial");
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }

    @Override
    public String generateAndUploadCertificateImage(String studentName, String courseName, Long certificateId) throws Exception {
        // 1. Generate PDF bytes
        byte[] pdfBytes = generateCertificatePdf(studentName, courseName);

        // 2. Render first page of PDF to PNG image at 150 DPI (PDFBox 2.x API)
        byte[] pngBytes;
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 150, ImageType.RGB);
            try (ByteArrayOutputStream imgOut = new ByteArrayOutputStream()) {
                ImageIO.write(image, "PNG", imgOut);
                pngBytes = imgOut.toByteArray();
            }
        }

        // 3. Upload PNG to Cloudinary
        String folder = "certificates";
        String filename = "cert_" + certificateId + "_" + System.currentTimeMillis();
        String url = cloudinaryService.uploadBytes(pngBytes, folder, filename);
        log.info("Certificate image uploaded to Cloudinary: {}", url);
        return url;
    }

    private Context buildContext(String studentName, String courseName) throws Exception {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("courseName", courseName);
        context.setVariable("issueDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        ClassPathResource imgFile = new ClassPathResource("static/images/certificate.png");
        byte[] imgBytes = Files.readAllBytes(imgFile.getFile().toPath());
        String base64Image = Base64.getEncoder().encodeToString(imgBytes);
        context.setVariable("bgImage", "data:image/jpeg;base64," + base64Image);
        return context;
    }
}
