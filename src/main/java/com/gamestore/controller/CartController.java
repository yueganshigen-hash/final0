package com.gamestore.controller;

import com.gamestore.dto.Result;
import com.gamestore.entity.*;
import com.gamestore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @GetMapping
    public Result<?> list(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return Result.success(List.of());
        User user = userRepository.findByUsername(ud.getUsername()).orElseThrow();
        return Result.success(cartItemRepository.findByUser(user));
    }

    @PostMapping
    public Result<CartItem> add(@RequestBody Map<String, Long> body,
                                @AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return Result.fail("请先登录");
        User user = userRepository.findByUsername(ud.getUsername()).orElseThrow();
        Game game = gameRepository.findById(body.get("gameId")).orElseThrow();
        var existing = cartItemRepository.findByUserAndGame(user, game);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            return Result.success(cartItemRepository.save(item));
        }
        CartItem item = new CartItem();
        item.setUser(user);
        item.setGame(game);
        return Result.success(cartItemRepository.save(item));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> remove(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return Result.fail("请先登录");
        cartItemRepository.deleteById(id);
        return Result.success();
    }

    @DeleteMapping
    @Transactional
    public Result<Void> clear(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return Result.fail("请先登录");
        User user = userRepository.findByUsername(ud.getUsername()).orElseThrow();
        cartItemRepository.deleteByUser(user);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Long> count(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return Result.success(0L);
        User user = userRepository.findByUsername(ud.getUsername()).orElseThrow();
        return Result.success(cartItemRepository.countByUser(user));
    }
}