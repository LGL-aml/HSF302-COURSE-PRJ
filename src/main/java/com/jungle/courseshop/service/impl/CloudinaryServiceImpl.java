package com.jungle.courseshop.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jungle.courseshop.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "/upload",
                "use_filename", true,
                "unique_filename", true,
                "resource_type", "auto"
        ));

        return result.get("secure_url").toString();
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    @Override
    public String uploadBytes(byte[] bytes, String folder, String filename) throws IOException {
        var result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                "folder", folder,
                "public_id", filename,
                "unique_filename", true,
                "resource_type", "image"
        ));

        return result.get("secure_url").toString();
    }
}
