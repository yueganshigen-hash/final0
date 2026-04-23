package com.gamestore.repository;

import com.gamestore.entity.Order;
import com.gamestore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNo(String orderNo);
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByGameMerchant(User merchant, Pageable pageable);
    long countByStatus(Order.OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal sumPaidAmount();
}
