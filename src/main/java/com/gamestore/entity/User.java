package com.gamestore.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // Merchant fields
    private String shopName;
    private String shopDescription;

    @Enumerated(EnumType.STRING)
    private MerchantStatus merchantStatus;  // null = not merchant, PENDING/APPROVED/REJECTED

    @Column(precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    private Boolean enabled = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Role {
        USER, MERCHANT, ADMIN
    }

    public enum MerchantStatus {
        PENDING, APPROVED, REJECTED
    }
}
