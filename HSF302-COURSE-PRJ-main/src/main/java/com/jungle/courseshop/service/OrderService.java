package com.jungle.courseshop.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface OrderService {

    boolean isCourseFree(Long courseId);

    void enrollFreeCourseFromCart(Long courseId);

    String createOrderFromCart(Long courseId) throws UnsupportedEncodingException;

    void handleVNPayCallback(String orderCode, boolean isSuccess) throws MessagingException, UnsupportedEncodingException;

    String handleVNPayIpn(Map<String, String> params) throws MessagingException, UnsupportedEncodingException;

    boolean processVnPayReturn(Map<String, String> params) throws UnsupportedEncodingException;
}

