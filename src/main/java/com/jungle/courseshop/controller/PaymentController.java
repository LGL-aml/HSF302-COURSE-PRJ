package com.jungle.courseshop.controller;

import com.jungle.courseshop.service.OrderService;
import com.jungle.courseshop.service.impl.OrderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;
//fix

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/payments")
public class PaymentController {
    private final OrderService orderService;


    @GetMapping("/checkout")
    public String checkout(@RequestParam(required = false) Long courseId,
                           RedirectAttributes redirectAttributes) {
        try {
            String paymentUrl = orderService.createOrderFromCart(courseId);

            return "redirect:" + paymentUrl;

        } catch (Exception e) {
            log.error("Lỗi khi tạo thanh toán", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, Model model) {
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        try {
            boolean isSuccess = orderService.processVnPayReturn(params);

            if (isSuccess) {
                model.addAttribute("message", "Thanh toán thành công! Bạn có thể vào học ngay.");
                return "payment/success";
            } else {
                model.addAttribute("message", "Thanh toán thất bại hoặc bị hủy.");
                return "payment/failed";
            }

        } catch (Exception e) {
            log.error("Lỗi xử lý callback VNPay", e);
            model.addAttribute("message", "Có lỗi xảy ra khi xử lý giao dịch: " + e.getMessage());
            return "payment/failed";
        }
    }

}
