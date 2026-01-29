package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.CartReponse;
import com.jungle.courseshop.service.CartService;
import com.jungle.courseshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping
    public String viewCart(Model model) {
        try {
            CartReponse cart = cartService.getCartByUser();
            model.addAttribute("cart", cart);
            model.addAttribute("title", "Giỏ hàng");
        } catch (Exception e) {
            log.error("Error loading cart", e);
            model.addAttribute("error", "Không thể tải giỏ hàng");
        }
        return "cart/index";
    }

    @PostMapping("/add/{courseId}")
    public String addToCart(@PathVariable Long courseId,
                           @RequestParam(required = false) String buyNow,
                           RedirectAttributes redirectAttributes) {
        try {
            cartService.addItemToCart(courseId);

            if ("true".equals(buyNow)) {
                // Nếu là "Mua ngay"
                // Khóa học miễn phí (0đ) thì đăng ký luôn, không qua thanh toán
                if (orderService.isCourseFree(courseId)) {
                    orderService.enrollFreeCourseFromCart(courseId);
                    redirectAttributes.addFlashAttribute("message", "Đăng ký khóa học thành công");
                    return "redirect:/courses/learn/" + courseId;
                }

                // Khóa học trả phí thì redirect đến thanh toán
                return "redirect:/payments/checkout?courseId=" + courseId;
            } else {
                // Nếu là "Thêm vào giỏ hàng", redirect đến giỏ hàng
                redirectAttributes.addFlashAttribute("message", "Đã thêm khóa học vào giỏ hàng");
                return "redirect:/cart";
            }
        } catch (Exception e) {
            log.error("Error adding to cart", e);
            redirectAttributes.addFlashAttribute("error", "Không thể thêm vào giỏ hàng: " + e.getMessage());
            return "redirect:/courses/" + courseId;
        }
    }

    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItemFromCart(itemId);
            redirectAttributes.addFlashAttribute("message", "Đã xóa khóa học khỏi giỏ hàng");
        } catch (Exception e) {
            log.error("Error removing from cart", e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa khỏi giỏ hàng");
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        try {
            // Redirect to payment checkout for all cart items
            return "redirect:/payments/checkout";
        } catch (Exception e) {
            log.error("Error during checkout", e);
            redirectAttributes.addFlashAttribute("error", "Không thể thanh toán");
            return "redirect:/cart";
        }
    }
}
