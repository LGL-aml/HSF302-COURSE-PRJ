package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.NotificationMessage;
import com.jungle.courseshop.entity.*;
import com.jungle.courseshop.repository.CartRepo;
import com.jungle.courseshop.repository.OrderItemRepo;
import com.jungle.courseshop.repository.OrderRepo;
import com.jungle.courseshop.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final CartService cartService;
    private final CartRepo cartRepo;
    private final OrderRepo orderRepository;
    private final OrderItemRepo orderItemRepository;
    private final VnPayService vnPayService;
    private final UserRepo userRepo;
    private final CourseEnrollmentService courseEnrollmentService;
    private final NotificationService notificationService;


    @Transactional
    public String createOrderFromCart(Long courseId) throws UnsupportedEncodingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepo.findByUserId(user.getId());

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setId(UUID.randomUUID().toString());

        BigDecimal total = BigDecimal.ZERO;

        List<CartItem> itemsToCheckout;

        if (courseId != null) {
            // thanh toán 1 khoá học
            itemsToCheckout = cart.getItems().stream()
                    .filter(i -> i.getCourse().getId().equals(courseId))
                    .toList();
            if (itemsToCheckout.isEmpty()) {
                throw new IllegalArgumentException("Course not found in cart");
            }
        } else {
            // thanh toán cả giỏ hàng
            itemsToCheckout = cart.getItems();
        }

        for (CartItem cartItem : itemsToCheckout) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCourse(cartItem.getCourse());
            orderItem.setPrice(cartItem.getCourse().getPrice());

            order.getItems().add(orderItem);
            total = total.add(cartItem.getCourse().getPrice());
        }

        order.setTotalAmount(total);
        orderRepository.save(order);


        // Tạo URL thanh toán VNPay
        return vnPayService.createPaymentUrl(order);
    }


    @Transactional
    public void handleVNPayCallback(String orderCode, boolean isSuccess) throws MessagingException, UnsupportedEncodingException {
        Order order = orderRepository.findById(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (isSuccess) {
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);
            User user = order.getUser();

            // enroll user vào course sau khi thanh toán thành công
            for (OrderItem item : order.getItems()) {
                Course course = item.getCourse();
                courseEnrollmentService.enrollUserToCourse(user, course);
                cartService.removeItemFromCart(user.getId(), course.getId());
            }

//            notificationService.notifyUser(
//                    user.getUsername(),
//                    new NotificationMessage(
//                            "Thanh toán thành công",
//                            "Đơn hàng #" + order.getId() + " đã được thanh toán",
//                            "SUCCESS",
//                            LocalDateTime.now()
//                    )
//            );
        } else {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
        }
    }

    @Transactional
    public String handleVNPayIpn(Map<String, String> params) throws MessagingException, UnsupportedEncodingException {
        // 1. Validate chữ ký
        if (!vnPayService.validateSignature(new HashMap<>(params))) {
            return "Invalid signature";
        }

        String orderCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        Order order = orderRepository.findById(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("00".equals(responseCode)) {
            if (order.getStatus() != OrderStatus.PAID) {
                order.setStatus(OrderStatus.PAID);
                order.setPaidAt(LocalDateTime.now());
                orderRepository.save(order);

                User user = order.getUser();
                for (OrderItem item : order.getItems()) {
                    courseEnrollmentService.enrollUserToCourse(user, item.getCourse());
                    cartService.removeItemFromCart(user.getId() , item.getCourse().getId());
                }

                log.info("Gửi thông báo tới user: " + user.getUsername());

//                notificationService.notifyUser(
//                        user.getUsername(),
//                        new NotificationMessage(
//                                "Thanh toán thành công",
//                                "Đơn hàng #" + order.getId() + " đã được xác nhận",
//                                "SUCCESS",
//                                LocalDateTime.now()
//                        )
//                );

            }


            return "OK";
        } else {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            return "FAILED";
        }
    }

    @Transactional
    public boolean processVnPayReturn(Map<String, String> params) throws UnsupportedEncodingException {
        // 1. Validate signature
        log.info("Xử lý VnPay Return với params: " + params);
        if (!vnPayService.validateSignature(new HashMap<>(params))) {
            throw new IllegalArgumentException("Chữ ký bảo mật không hợp lệ");
        }

        String orderCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        // 2. find order
        Order order = orderRepository.findById(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderCode));

        // 3. check order status
        if ("00".equals(responseCode)) {
            if (order.getStatus() != OrderStatus.PAID) {
                finalizeSuccessfulOrder(order);
            }
            return true;
        } else {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            return false;
        }


    }

    private void finalizeSuccessfulOrder(Order order) {
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        User user = order.getUser();

        // Enroll user & Xóa giỏ hàng
        for (OrderItem item : order.getItems()) {
            Course course = item.getCourse();
            try {
                // Enroll (trong hàm enrollUserToCourse nên có check exists rồi)
                courseEnrollmentService.enrollUserToCourse(user, course);
                // Xóa khỏi giỏ hàng
                cartService.removeItemFromCart(user.getId(), course.getId());
            } catch (Exception e) {
                log.error("Lỗi khi kích hoạt khóa học sau thanh toán", e);
            }
        }

        // Gửi thông báo
//        try {
//            notificationService.notifyUser(
//                    user.getUsername(),
//                    new NotificationMessage("Thanh toán thành công",
//                            "Đơn hàng #" + order.getId() + " hoàn tất.",
//                            "SUCCESS", LocalDateTime.now())
//            );
//        } catch (Exception e) {
//            log.error("Lỗi gửi thông báo", e);
//        }
    }


}

