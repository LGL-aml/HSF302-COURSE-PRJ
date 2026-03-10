package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.UserCardResponse;
import com.jungle.courseshop.dto.response.VnptFaceCompareResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VnptKycService {
    UserCardResponse extractIdCardInfo(MultipartFile frontImage,
                                       MultipartFile backImage);

    // Overload: extract from pre-uploaded hashes
    UserCardResponse extractIdCardInfoFromHashes(String frontHash, String backHash);

    String uploadCV(MultipartFile cvFile) throws Exception;

    VnptFaceCompareResponse compareFace(MultipartFile portraitImage, String frontCardHash);

    // New: compare face from base64 string captured via webcam
    VnptFaceCompareResponse compareFaceFromBase64(String portraitBase64, String frontCardHash);

    // Upload file to VNPT and return hash
    String uploadFileToVnpt(MultipartFile file);
}
