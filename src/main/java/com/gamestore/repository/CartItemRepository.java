package com.gamestore.repository;

import com.gamestore.entity.CartItem;
import com.gamestore.entity.Game;
import com.gamestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndGame(User user, Game game);

    long countByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user = :user")
    void deleteByUser(@Param("user") User user);
}