package com.jungle.courseshop.service;

public interface CertificateService {

    byte[] generateCertificatePdf(String studentName, String courseName) throws Exception;

    String generateAndUploadCertificateImage(String studentName, String courseName, Long certificateId) throws Exception;
}
