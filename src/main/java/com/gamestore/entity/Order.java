package com.gamestore.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private String cdkCode;      // filled after payment
    private LocalDateTime paidAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum OrderStatus {
        PENDING, PAID, CANCELLED, REFUNDED
    }
}
