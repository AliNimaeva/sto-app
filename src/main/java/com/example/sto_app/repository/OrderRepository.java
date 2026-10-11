package com.example.sto_app.repository;

import com.example.sto_app.model.Order;
import com.example.sto_app.model.OrderStatus;
import com.example.sto_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByNumber(String number);

    List<Order> findByMaster(User master);

   // List<Order> findByStatus(OrderStatus status);

    long countByStatus(OrderStatus status);

    long countByPaidTrue();

    long countByPaidFalse();

    Page<Order> findByStatus(
            OrderStatus status,
            Pageable pageable
    );

    List<Order> findByMasterAndStatus(User master, OrderStatus status);

    boolean existsByMasterAndStatusIn(
            User master,
            List<OrderStatus> statuses
    );

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime from,
            LocalDateTime to
    );

    long countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    long countByPaidTrueAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime from,
            LocalDateTime to
    );

    List<Order> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime from,
            LocalDateTime to
    );
}