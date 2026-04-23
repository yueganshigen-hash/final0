package com.gamestore.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String coverImage;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private BigDecimal originalPrice;

    @Column(length = 50)
    private String genre;       // Action, RPG, Strategy, etc.

    @Column(length = 50)
    private String platform;    // PC, Console, etc.

    private String developer;
    private String publisher;

    private Integer stock = 0;
    private Integer soldCount = 0;
    private Double rating = 0.0;
    private Integer ratingCount = 0;

    private Boolean isHot = false;
    private Boolean isFlashSale = false;
    private BigDecimal flashSalePrice;
    private LocalDateTime flashSaleEnd;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.ON_SALE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private User merchant;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum GameStatus {
        ON_SALE, OFF_SALE, PENDING_REVIEW
    }
}
