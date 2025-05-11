package com.carrito.cart_service.repository;

import com.carrito.cart_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Long> findIdsByUserId(@Param("userId") Long userId);
    List<Order> findAllByUserId(Long userId);
}
