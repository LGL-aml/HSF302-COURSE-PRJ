package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.UserCardResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VnptKycService {
    UserCardResponse extractIdCardInfo(MultipartFile frontImage,
                                       MultipartFile backImage);

    String uploadCV(MultipartFile cvFile) throws Exception;
}
