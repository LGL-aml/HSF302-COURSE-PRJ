package com.jungle.courseshop.service;

import com.jungle.courseshop.entity.Order;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface VnPayService {

    String createPaymentUrl(Order order) throws UnsupportedEncodingException;

    boolean validateSignature(Map<String, String> params);
}

