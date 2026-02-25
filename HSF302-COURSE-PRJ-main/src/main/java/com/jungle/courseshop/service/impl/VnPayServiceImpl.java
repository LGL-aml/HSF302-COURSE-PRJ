package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.entity.Order;
import com.jungle.courseshop.service.VnPayService;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayServiceImpl implements VnPayService {
    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.ipn-url}")
    private String ipnUrl;

    @Override
    public String createPaymentUrl(Order order) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", order.getId());
        params.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
//        params.put("vnp_IpnUrl", ipnUrl);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String field = fieldNames.get(i);
            String value = params.get(field);
            if (value != null && value.length() > 0) {
                hashData.append(field).append("=").append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(field, StandardCharsets.US_ASCII.toString()))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return payUrl + "?" + query.toString();
    }

//    public boolean validateSignature(Map<String, String> params) {
//        String secureHash = params.remove("vnp_SecureHash");
//        String query = params.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .map(e -> e.getKey() + "=" + e.getValue())
//                .collect(Collectors.joining("&"));
//
//        String myHash = hmacSHA512(hashSecret, query);
//        return myHash.equalsIgnoreCase(secureHash);
//    }

    @Override
    public boolean validateSignature(Map<String, String> params) {
        // 1. Tạo bản sao của Map để xử lý (tránh lỗi UnsupportedOperationException khi remove key)
        Map<String, String> paramsCopy = new HashMap<>(params);
        String vnp_SecureHash = paramsCopy.remove("vnp_SecureHash");
        paramsCopy.remove("vnp_SecureHashType");

        if (vnp_SecureHash == null) {
            return false;
        }

        try {
            // 3. Sắp xếp các tham số theo A-Z
            List<String> fieldNames = new ArrayList<>(paramsCopy.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = paramsCopy.get(fieldName);

                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    // Build chuỗi: key=encodedValue
                    hashData.append(fieldName);
                    hashData.append("=");
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                    if (itr.hasNext()) {
                        hashData.append("&");
                    }
                }
            }

            // 4. Hash dữ liệu mình vừa tạo lại
            String myHash = hmacSHA512(hashSecret, hashData.toString());

            return myHash.equalsIgnoreCase(vnp_SecureHash);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(bytes).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating HMAC", e);
        }
    }
}
