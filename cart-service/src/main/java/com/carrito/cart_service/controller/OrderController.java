package com.carrito.cart_service.controller;

import com.carrito.cart_service.dto.OrdersResponse;
import com.carrito.cart_service.model.Order;
import com.carrito.cart_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrdersResponse>> getOrders(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        List<OrdersResponse> orders = orderService.findAllByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Long>> checkout(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        Long orderId = orderService.checkout(userId);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrdersResponse> getOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long orderId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        OrdersResponse order = orderService.findById(orderId, userId);
        return ResponseEntity.ok(order);
    }
}

