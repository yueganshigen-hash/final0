package com.gamestore.service;

import com.gamestore.entity.*;
import com.gamestore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final CdkRepository cdkRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Order createOrder(Long gameId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));
        if (game.getStock() <= 0)
            throw new RuntimeException("库存不足");

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setGame(game);
        order.setAmount(game.getIsFlashSale() != null && game.getIsFlashSale()
                ? game.getFlashSalePrice() : game.getPrice());
        order.setStatus(Order.OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Transactional
    public Order payOrder(String orderNo, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getUser().getId().equals(user.getId()))
            throw new RuntimeException("无权操作");
        if (order.getStatus() != Order.OrderStatus.PENDING)
            throw new RuntimeException("订单状态异常");

        // Assign CDK
        Cdk cdk = cdkRepository.findFirstByGameAndIsUsedFalse(order.getGame())
                .orElseThrow(() -> new RuntimeException("CDK库存不足，请联系客服"));

        cdk.setIsUsed(true);
        cdk.setUser(user);
        cdk.setOrderNo(orderNo);
        cdk.setUsedAt(LocalDateTime.now());
        cdkRepository.save(cdk);

        // Update game stock
        Game game = order.getGame();
        game.setStock(game.getStock() - 1);
        game.setSoldCount(game.getSoldCount() + 1);
        gameRepository.save(game);

        // Mark order paid
        order.setStatus(Order.OrderStatus.PAID);
        order.setCdkCode(cdk.getCode());
        order.setPaidAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Page<Order> getMyOrders(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return orderRepository.findByUser(user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Order getOrderDetail(String orderNo, String username) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getUser().getUsername().equals(username))
            throw new RuntimeException("无权查看");
        return order;
    }

    @Transactional
    public Order cancelOrder(String orderNo, String username) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getUser().getUsername().equals(username))
            throw new RuntimeException("无权操作");
        if (order.getStatus() != Order.OrderStatus.PENDING)
            throw new RuntimeException("只能取消待支付订单");
        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    private String generateOrderNo() {
        return "GS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
