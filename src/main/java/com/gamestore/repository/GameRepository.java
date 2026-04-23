package com.gamestore.repository;

import com.gamestore.entity.Game;
import com.gamestore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Page<Game> findByStatus(Game.GameStatus status, Pageable pageable);

    Page<Game> findByMerchantAndStatus(User merchant, Game.GameStatus status, Pageable pageable);

    Page<Game> findByMerchant(User merchant, Pageable pageable);

    List<Game> findTop8ByIsHotTrueAndStatus(Game.GameStatus status);

    List<Game> findTop8ByIsFlashSaleTrueAndStatus(Game.GameStatus status);

    List<Game> findTop8ByStatusOrderByCreatedAtDesc(Game.GameStatus status);

    @Query("SELECT g FROM Game g WHERE g.status = 'ON_SALE' AND " +
           "(LOWER(g.name) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(g.genre) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(g.developer) LIKE LOWER(CONCAT('%',:kw,'%')))")
    Page<Game> search(@Param("kw") String keyword, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.status = 'ON_SALE' AND (:genre IS NULL OR g.genre = :genre)")
    Page<Game> findByGenre(@Param("genre") String genre, Pageable pageable);

    long countByMerchant(User merchant);

    long countByStatus(Game.GameStatus status);
}
