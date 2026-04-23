package com.gamestore.controller;

import com.gamestore.dto.Result;
import com.gamestore.entity.*;
import com.gamestore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userRepository.countByRole(User.Role.USER));
        data.put("totalMerchants", userRepository.countByRole(User.Role.MERCHANT));
        data.put("totalGames", gameRepository.countByStatus(Game.GameStatus.ON_SALE));
        data.put("totalOrders", orderRepository.countByStatus(Order.OrderStatus.PAID));
        data.put("totalRevenue", orderRepository.sumPaidAmount());
        return Result.success(data);
    }

    @GetMapping("/users")
    public Result<Page<User>> users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userRepository.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/orders")
    public Result<Page<Order>> orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/users/{id}/toggle")
    public Result<Void> toggleUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
        return Result.success();
    }
}
