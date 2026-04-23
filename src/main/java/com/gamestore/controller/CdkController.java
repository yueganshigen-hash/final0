package com.gamestore.controller;

import com.gamestore.dto.Result;
import com.gamestore.entity.Cdk;
import com.gamestore.entity.Game;
import com.gamestore.repository.CdkRepository;
import com.gamestore.repository.GameRepository;
import com.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cdk")
@RequiredArgsConstructor
public class CdkController {

    private final CdkRepository cdkRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @GetMapping("/my")
    public Result<List<Cdk>> myCdks(@AuthenticationPrincipal UserDetails ud) {
        var user = userRepository.findByUsername(ud.getUsername()).orElseThrow();
        return Result.success(cdkRepository.findByUser(user));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public Result<Map<String, Object>> importCdks(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails ud) {
        Long gameId = Long.valueOf(body.get("gameId").toString());
        @SuppressWarnings("unchecked")
        List<String> codes = (List<String>) body.get("codes");

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));

        int count = 0;
        for (String code : codes) {
            if (code == null || code.isBlank()) continue;
            Cdk cdk = new Cdk();
            cdk.setCode(code.trim());
            cdk.setGame(game);
            cdkRepository.save(cdk);
            count++;
        }
        // update stock
        game.setStock(game.getStock() + count);
        gameRepository.save(game);

        return Result.success(Map.of("count", count));
    }

    @GetMapping("/stock/{gameId}")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public Result<Map<String, Long>> stock(@PathVariable Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        long total = cdkRepository.countByGame(game);
        long available = cdkRepository.countByGameAndIsUsedFalse(game);
        return Result.success(Map.of("total", total, "available", available, "used", total - available));
    }
}
