package com.hsf302.ecommerce.repository;

import com.hsf302.ecommerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT x FROM Order x JOIN User u WHERE u.username = :username")
    Page<Order> findAllByUsername(
            @Param("username") String username,
            Pageable pageable);
}
