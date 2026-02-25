package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.PostCodeDTO;
import com.jungle.courseshop.dto.VnptOcrDTO;
import com.jungle.courseshop.dto.request.VnptClassifyRequest;
import com.jungle.courseshop.dto.request.VnptFaceCompareRequest;
import com.jungle.courseshop.dto.request.VnptOcrFullRequest;
import com.jungle.courseshop.dto.response.*;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.UserRepo;
import com.jungle.courseshop.service.CloudinaryService;
import com.jungle.courseshop.service.VnptKycService;
import com.jungle.courseshop.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnptKycServiceImpl implements VnptKycService {
    private final WebClient vnptWebClient;

    private final DateUtils dateUtils;

    private final UserRepo userRepo;

//    JwtHelper jwtHelper;

    private final CloudinaryService cloudinaryService;


    @Override
    public UserCardResponse extractIdCardInfo(MultipartFile frontImage,
                                              MultipartFile backImage) {
//        User user = userRepo.findById(jwtHelper.getCurrentUserId())
//                .orElseThrow(() -> new GetException("User not found with id: " + jwtHelper.getCurrentUserId()));
        String initialClientSession = "user" + "_" + System.currentTimeMillis();
        log.info("Đang upload mặt trước...");
        String frontHash = uploadFile(frontImage);
        log.info("Đang upload mặt sau...");
        String backHash = uploadFile(backImage);

        String safeClientSession = initialClientSession.replaceAll("[^a-zA-Z0-9]", "");
        String transactionToken = UUID.randomUUID().toString().replace("-", "");
        Integer idType = callClassifyApi(frontHash, safeClientSession, transactionToken);
        log.info("API Phân loại trả về type: {}", idType);
        validateDocumentType(idType);

        // Request body
        VnptOcrFullRequest requestBody = new VnptOcrFullRequest();
        requestBody.setImgFront(frontHash);
        requestBody.setImgBack(backHash);
        requestBody.setClientSession(safeClientSession);
        requestBody.setType(idType);
        requestBody.setCropParam("");
        requestBody.setValidatePostcode(true);
        requestBody.setToken(transactionToken);

        // Call api 6
        VnptOcrFullResponse response = callFullOcrApi(requestBody);

        // Map data
        if (response == null || response.getObject() == null) {
            log.error("API VNPT trả về rỗng hoặc không có 'object'");
            throw new IllegalArgumentException("Không bóc tách được dữ liệu (object is null)");
        }
        VnptOcrDTO data = response.getObject();
        data.setIssuePlace(data.getIssuePlace().replace("/n", " "));
        //        userCardResponse.setCardImages(generateCardImageDTOs(frontImage, backImage));
//        log.info("Bóc tách thành công User: {}", user.getFullName());
        return mapDataToUser(data);
    }

    @Override
    public UserCardResponse extractIdCardInfoFromHashes(String frontHash, String backHash) {
        String initialClientSession = "user" + "_" + System.currentTimeMillis();
        String safeClientSession = initialClientSession.replaceAll("[^a-zA-Z0-9]", "");
        String transactionToken = UUID.randomUUID().toString().replace("-", "");

        Integer idType = callClassifyApi(frontHash, safeClientSession, transactionToken);
        log.info("API Phân loại trả về type: {}", idType);
        validateDocumentType(idType);

        VnptOcrFullRequest requestBody = new VnptOcrFullRequest();
        requestBody.setImgFront(frontHash);
        requestBody.setImgBack(backHash);
        requestBody.setClientSession(safeClientSession);
        requestBody.setType(idType);
        requestBody.setCropParam("");
        requestBody.setValidatePostcode(true);
        requestBody.setToken(transactionToken);

        VnptOcrFullResponse response = callFullOcrApi(requestBody);

        if (response == null || response.getObject() == null) {
            log.error("API VNPT trả về rỗng hoặc không có 'object'");
            throw new IllegalArgumentException("Không bóc tách được dữ liệu (object is null)");
        }
        VnptOcrDTO data = response.getObject();
        data.setIssuePlace(data.getIssuePlace().replace("/n", " "));
        return mapDataToUser(data);
    }

    @Override
    public String uploadFileToVnpt(MultipartFile file) {
        return uploadFile(file);
    }

//    private List<CardImgDTO> generateCardImageDTOs(MultipartFile frontImage,
//                                                   MultipartFile backImage) {
//        try{
//            String frontImageUrl = cloudinaryService.uploadFile(frontImage);
//            String backImageUrl = cloudinaryService.uploadFile(backImage);
//            return Stream.of(
//                            new String[]{"front", "Ảnh mặt trước CCCD", frontImageUrl},
//                            new String[]{"back", "Ảnh mặt sau CCCD", backImageUrl}
//                    )
//                    .map(arr -> CardImgDTO.builder()
//                            .type(arr[0])
//                            .description(arr[1])
//                            .imageUrl(arr[2])
//                            .build())
//                    .toList();
//        }catch(IOException e){
//            log.error("Lỗi khi upload ảnh lên Cloudinary: {}", e.getMessage(), e);
//            throw new IllegalArgumentException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage(), e);
//        }
//    }

    private void validateDocumentType(Integer idType) {
        List<Integer> allowedTypes = Arrays.asList(-1, 0, 2);
        if (allowedTypes.contains(idType)) {
            return;
        }

        log.warn("Loại giấy tờ không được hỗ trợ. Type: {}", idType);

        String errorMessage = switch (idType) {
            case 1, 3 -> "Loại giấy tờ không khớp với mặt trước (CCCD).";
            case 4 -> "Loại giấy tờ không được hỗ trợ (Giấy tờ khác).";
            case 5 -> "Loại giấy tờ không được hỗ trợ (Hộ chiếu).";
            default -> "Không thể xác định loại giấy tờ hoặc giấy tờ không được hỗ trợ. Vui lòng thử lại.";
        };

        throw new IllegalArgumentException(errorMessage);
    }


    private Integer callClassifyApi(String imageHash, String clientSession, String token) {
        log.info("Đang gọi API Phân loại giấy tờ (/ai/v1/classify/id)...");

        // Tạo body
        VnptClassifyRequest requestBody = VnptClassifyRequest.builder()
                .imgCard(imageHash)
                .clientSession(clientSession)
                .token(token)
                .build();

        try {
            VnptClassifyResponse response = vnptWebClient.post()
                    .uri("/ai/v1/classify/id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("mac-address", "TEST1")
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(VnptClassifyResponse.class)
                    .block();

            // Lấy type từ object con
            if (response != null && response.getObject() != null && response.getObject().getType() != null) {
                return response.getObject().getType();
            } else {
                log.warn("Phân loại giấy tờ trả về 200 OK nhưng không có 'object' hoặc 'type'. Dùng type -1.");
                return -2;
            }

        } catch (WebClientResponseException e) {
            log.error("LỖI API VNPT (Classify): Status code {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Lỗi khi gọi API Phân loại: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("LỖI HỆ THỐNG (Classify): {}", e.getMessage(), e);
            throw new IllegalArgumentException("Lỗi hệ thống khi gọi API Phân loại: " + e.getMessage(), e);
        }
    }


    private VnptOcrFullResponse callFullOcrApi(VnptOcrFullRequest requestBody) {
        log.info("Đang gọi API OCR gộp (/ai/v1/ocr/id)...");
        try {
            return vnptWebClient.post()
                    .uri("/ai/v1/ocr/id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("mac-address", "TEST1")
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(VnptOcrFullResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("LỖI API VNPT (Full OCR): Status code {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Lỗi khi gọi API Bóc tách gộp: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("LỖI HỆ THỐNG (Full OCR): {}", e.getMessage(), e);
            throw new IllegalArgumentException("Lỗi hệ thống khi gọi API Bóc tách gộp: " + e.getMessage(), e);
        }
    }


    private String uploadFile(MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("title", "CCCD Upload");
        body.add("description", "Upload file eKYC cho session: user_123");

        try {
            VnptUploadResponse response = vnptWebClient.post()
                    .uri("/file-service/v1/addFile")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(VnptUploadResponse.class)
                    .block();

            if (response != null && response.getObject() != null && response.getObject().getHash() != null) {
                return response.getObject().getHash();
            } else {
                log.info("Upload file thất bại hoặc response không có hash.");
                throw new IllegalArgumentException("Upload file thất bại hoặc response không có hash.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi khi gọi API Upload: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadCV(MultipartFile cvFile) throws Exception {
        log.info("Đang upload CV lên Cloudinary...");

        // Validate file
        if (cvFile == null || cvFile.isEmpty()) {
            throw new IllegalArgumentException("File CV không được để trống");
        }

        // Validate file size (10MB)
        if (cvFile.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file CV không được vượt quá 10MB");
        }

        // Validate file type
        String contentType = cvFile.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf")
                && !contentType.equals("application/msword")
                && !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Chỉ chấp nhận file PDF, DOC, DOCX");
        }

        try {
            // Upload lên Cloudinary
            String cvUrl = cloudinaryService.uploadFile(cvFile);
            log.info("Upload CV thành công: {}", cvUrl);
            return cvUrl;
        } catch (IOException e) {
            log.error("Lỗi khi upload CV lên Cloudinary: {}", e.getMessage(), e);
            throw new Exception("Lỗi khi upload CV: " + e.getMessage(), e);
        }
    }

    @Override
    public VnptFaceCompareResponse compareFace(MultipartFile portraitImage, String frontCardHash) {
        // 1. Khởi tạo session và token
        String safeClientSession = ("user_" + System.currentTimeMillis()).replaceAll("[^a-zA-Z0-9]", "");
        String transactionToken = UUID.randomUUID().toString().replace("-", "");

        // 2. Upload ảnh chân dung lên VNPT để lấy hash (Sử dụng hàm uploadFile có sẵn của bạn)
        log.info("Đang upload ảnh chân dung selfie...");
        String portraitHash = uploadFile(portraitImage);

        // 3. Tạo body request
        VnptFaceCompareRequest requestBody = VnptFaceCompareRequest.builder()
                .img_front(frontCardHash)
                .img_face(portraitHash)
                .client_session(safeClientSession)
                .token(transactionToken)
                .build();

        // 4. Gọi API VNPT Face Compare
        log.info("Đang gọi API xác thực khuôn mặt (/ai/v1/face/compare)...");
        try {
            VnptFaceCompareResponse response = vnptWebClient.post()
                    .uri("/ai/v1/face/compare")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("mac-address", "TEST1") // Thay bằng MAC thực tế nếu cần
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(VnptFaceCompareResponse.class)
                    .block();

            if (response == null || response.getObject() == null) {
                throw new IllegalArgumentException("Không thể so khớp khuôn mặt (Response rỗng)");
            }

            log.info("Kết quả so khớp: {} - Độ tin cậy: {}%",
                    response.getObject().getMsg(),
                    response.getObject().getProb());

            return response;

        } catch (WebClientResponseException e) {
            log.error("LỖI API VNPT (Face Compare): {}", e.getResponseBodyAsString());
            throw new IllegalArgumentException("Lỗi xác thực khuôn mặt: " + e.getResponseBodyAsString());
        }
    }

    private UserCardResponse mapDataToUser(VnptOcrDTO data) {
        String address = formatAddressFromPostCode(data.getNewPostCode());
        return UserCardResponse.builder()
                .identifyNumber(data.getId())
                .fullName(data.getName())
                .birthDate(data.getBirthDay())
                .nationality(data.getNationality())
                .recentLocation(address != null ? address : data.getRecentLocation().replace("\n", ", "))
                .validDate(data.getValidDate())
                .issueDate(data.getIssueDate())
                .gender(data.getGender())
                .features(data.getFeatures())
                .issuePlace(data.getIssuePlace())
                .build();
    }

    private String formatAddressFromPostCode(List<PostCodeDTO> postCodes) {
        if (postCodes == null || postCodes.isEmpty()) return null;
        PostCodeDTO addressData = postCodes.stream()
                .filter(p -> "address".equalsIgnoreCase(p.getType()))
                .findFirst()
                .orElse(null);

        if (addressData == null) return null;
        try {
            String detail = addressData.getDetail();
            String ward = addressData.getWard().get(1).toString().trim();
            String district = addressData.getDistrict().get(1).toString().trim();
            String city = addressData.getCity().get(1).toString().trim();
            return Stream.of(detail, ward, district, city)
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            log.warn("Lỗi khi format địa chỉ từ new_post_code: {}", e.getMessage());
            return null;
        }
    }
}
