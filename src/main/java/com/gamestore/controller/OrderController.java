package com.gamestore.controller;

import com.gamestore.dto.Result;
import com.gamestore.entity.Order;
import com.gamestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Create order from game
    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody Map<String, Long> body,
                                      @AuthenticationPrincipal UserDetails ud) {
        Long gameId = body.get("gameId");
        return Result.success(orderService.createOrder(gameId, ud.getUsername()));
    }

    @GetMapping("/orders/my")
    public Result<Page<Order>> myOrders(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderService.getMyOrders(ud.getUsername(), page, size));
    }

    @GetMapping("/orders/{orderNo}")
    public Result<Order> detail(@PathVariable String orderNo,
                                 @AuthenticationPrincipal UserDetails ud) {
        return Result.success(orderService.getOrderDetail(orderNo, ud.getUsername()));
    }

    @PatchMapping("/orders/{orderNo}/cancel")
    public Result<Order> cancel(@PathVariable String orderNo,
                                 @AuthenticationPrincipal UserDetails ud) {
        return Result.success(orderService.cancelOrder(orderNo, ud.getUsername()));
    }

    // Virtual pay endpoint
    @PostMapping("/pay/{orderNo}")
    public Result<Map<String, String>> pay(@PathVariable String orderNo,
                                            @AuthenticationPrincipal UserDetails ud) {
        Order order = orderService.payOrder(orderNo, ud.getUsername());
        return Result.success(Map.of(
                "orderNo", order.getOrderNo(),
                "cdkCode", order.getCdkCode()
        ));
    }
}
