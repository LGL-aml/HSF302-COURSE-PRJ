package com.jungle.courseshop.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

    String uploadFile(MultipartFile file) throws IOException;

    void deleteFile(String publicId) throws IOException;

    String uploadBytes(byte[] bytes, String folder, String filename) throws IOException;
}
