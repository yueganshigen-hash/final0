package com.gamestore.repository;

import com.gamestore.entity.Cdk;
import com.gamestore.entity.Game;
import com.gamestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CdkRepository extends JpaRepository<Cdk, Long> {
    Optional<Cdk> findFirstByGameAndIsUsedFalse(Game game);
    List<Cdk> findByUser(User user);
    long countByGameAndIsUsedFalse(Game game);
    long countByGame(Game game);
}
