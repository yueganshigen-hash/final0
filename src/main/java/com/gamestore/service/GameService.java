package com.gamestore.service;

import com.gamestore.entity.Game;
import com.gamestore.entity.User;
import com.gamestore.repository.GameRepository;
import com.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public Page<Game> getGames(String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (genre != null && !genre.isBlank()) {
            return gameRepository.findByGenre(genre, pageable);
        }
        return gameRepository.findByStatus(Game.GameStatus.ON_SALE, pageable);
    }

    public Game getDetail(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));
    }

    public List<Game> getHot() {
        return gameRepository.findTop8ByIsHotTrueAndStatus(Game.GameStatus.ON_SALE);
    }

    public List<Game> getFlashSale() {
        return gameRepository.findTop8ByIsFlashSaleTrueAndStatus(Game.GameStatus.ON_SALE);
    }

    public List<Game> getNewRelease() {
        return gameRepository.findTop8ByStatusOrderByCreatedAtDesc(Game.GameStatus.ON_SALE);
    }

    public Page<Game> search(String keyword, int page, int size) {
        return gameRepository.search(keyword, PageRequest.of(page, size));
    }

    // Merchant operations
    public Page<Game> getMerchantGames(String username, int page, int size) {
        User merchant = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return gameRepository.findByMerchant(merchant, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Game addGame(Game game, String username) {
        User merchant = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        game.setMerchant(merchant);
        game.setStatus(Game.GameStatus.ON_SALE);
        return gameRepository.save(game);
    }

    public Game updateGame(Long id, Game update, String username) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));
        if (!game.getMerchant().getUsername().equals(username)) {
            throw new RuntimeException("无权操作");
        }
        if (update.getName() != null) game.setName(update.getName());
        if (update.getDescription() != null) game.setDescription(update.getDescription());
        if (update.getPrice() != null) game.setPrice(update.getPrice());
        if (update.getStock() != null) game.setStock(update.getStock());
        if (update.getCoverImage() != null) game.setCoverImage(update.getCoverImage());
        if (update.getGenre() != null) game.setGenre(update.getGenre());
        if (update.getIsFlashSale() != null) game.setIsFlashSale(update.getIsFlashSale());
        if (update.getFlashSalePrice() != null) game.setFlashSalePrice(update.getFlashSalePrice());
        return gameRepository.save(game);
    }

    public void deleteGame(Long id, String username) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("游戏不存在"));
        if (!game.getMerchant().getUsername().equals(username)) {
            throw new RuntimeException("无权操作");
        }
        game.setStatus(Game.GameStatus.OFF_SALE);
        gameRepository.save(game);
    }
}
