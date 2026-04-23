package com.gamestore.controller;

import com.gamestore.dto.Result;
import com.gamestore.entity.Game;
import com.gamestore.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public Result<Page<Game>> list(
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.success(gameService.getGames(genre, page, size));
    }

    @GetMapping("/{id}")
    public Result<Game> detail(@PathVariable Long id) {
        return Result.success(gameService.getDetail(id));
    }

    @GetMapping("/hot")
    public Result<List<Game>> hot() {
        return Result.success(gameService.getHot());
    }

    @GetMapping("/flash-sale")
    public Result<List<Game>> flashSale() {
        return Result.success(gameService.getFlashSale());
    }

    @GetMapping("/new-release")
    public Result<List<Game>> newRelease() {
        return Result.success(gameService.getNewRelease());
    }

    @GetMapping("/search")
    public Result<Page<Game>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.success(gameService.search(keyword, page, size));
    }

    // Merchant APIs
    @GetMapping("/mine")
    public Result<Page<Game>> myGames(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(gameService.getMerchantGames(ud.getUsername(), page, size));
    }

    @PostMapping
    public Result<Game> add(@RequestBody Game game, @AuthenticationPrincipal UserDetails ud) {
        return Result.success(gameService.addGame(game, ud.getUsername()));
    }

    @PutMapping("/{id}")
    public Result<Game> update(@PathVariable Long id, @RequestBody Game game,
                                @AuthenticationPrincipal UserDetails ud) {
        return Result.success(gameService.updateGame(id, game, ud.getUsername()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        gameService.deleteGame(id, ud.getUsername());
        return Result.success();
    }
}
