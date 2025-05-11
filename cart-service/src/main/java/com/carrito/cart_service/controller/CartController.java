package com.carrito.cart_service.controller;

import com.carrito.cart_service.dto.CartItemResponse;
import com.carrito.cart_service.model.CartItem;
import com.carrito.cart_service.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class CartController {
    private final CartService service;
    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        return ResponseEntity.ok(service.listItems(userId));
    }

    public static class CartItemRequest {
        @NotNull
        private Long productId;
        @NotNull @Min(1)
        private Integer quantity;
        public CartItemRequest() {}
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    @PostMapping
    public ResponseEntity<CartItem> addOrUpdate(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CartItemRequest req
    ) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        CartItem updated = service.addOrUpdateItem(
                userId,
                req.getProductId(),
                req.getQuantity()
        );
        return updated == null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId
    ) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        service.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
