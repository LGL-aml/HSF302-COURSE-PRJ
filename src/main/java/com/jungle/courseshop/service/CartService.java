package com.jungle.courseshop.service;

import com.jungle.courseshop.dto.response.CartReponse;
import com.jungle.courseshop.entity.Cart;
import com.jungle.courseshop.entity.CartItem;
import com.jungle.courseshop.entity.Course;
import com.jungle.courseshop.entity.User;
import com.jungle.courseshop.repository.CartItemRepo;
import com.jungle.courseshop.repository.CartRepo;
import com.jungle.courseshop.repository.CourseRepo;
import com.jungle.courseshop.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepo cartRepository;
    private final CartItemRepo cartItemRepository;
    private final UserRepo userRepository;
    private final CourseRepo courseRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Lấy giỏ hàng theo user
    public CartReponse getCartByUser() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId());
        if (cart == null) {
            // Nếu user chưa có giỏ hàng -> trả về giỏ rỗng
            return CartReponse.builder()
                    .cartId(null)
                    .userId(currentUser.getId())
                    .items(Collections.emptyList())
                    .totalPrice(BigDecimal.ZERO)
                    .build();
        }

        // Map CartItem -> CartItemResponse
        List<CartReponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartReponse.CartItemResponse.builder()
                        .courseId(item.getCourse().getId())
                        .courseTitle(item.getCourse().getTitle())
                        .price(item.getCourse().getPrice()) // BigDecimal
                        .courseThumbnail(item.getCourse().getCoverImage())
                        .lecturerName(item.getCourse().getCreator().getFullname())
                        .build()
                )
                .collect(Collectors.toList());

        // Tính tổng giá giỏ hàng
        BigDecimal totalCartPrice = itemResponses.stream()
                .map(CartReponse.CartItemResponse::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartReponse.builder()
                .cartId(cart.getId())
                .userId(currentUser.getId())
                .items(itemResponses)
                .totalPrice(totalCartPrice)
                .build();
    }

    // Thêm item vào giỏ
    public Cart addItemToCart(Long courseId) {
        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findByUserId(currentUser.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(currentUser);
            cart = cartRepository.save(cart);
        }

        // kiểm tra course đã có trong giỏ chưa
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getCourse().getId().equals(courseId))
                .findFirst();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (existingItem.isPresent()) {
            throw new RuntimeException("Course already exists in the cart");
        } else {
            CartItem newItem = new CartItem();
            newItem.setCourse(course);
            newItem.setCart(cart);
            newItem.setPrice(course.getPrice());
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    // Xóa item khỏi giỏ
    public void removeItemFromCart(Long userId, Long courseId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        cart.getItems().removeIf(item -> item.getCourse().getId().equals(courseId));
        cartRepository.save(cart);
    }

    public void removeItemFromCart(Long courseId) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId());
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        cart.getItems().removeIf(item -> item.getCourse().getId().equals(courseId));
        cartRepository.save(cart);
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId());
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }
}
