package com.carrito.cart_service.dto;

import java.util.List;

public class OrdersResponse {
    private Long id;
    private String createdAt;
    List<CartItemResponse> items;
    
    public OrdersResponse() {}
    
    public OrdersResponse(Long id, String createdAt, List<CartItemResponse> cartItems) {
        this.id = id;
        this.createdAt = createdAt;
        this.items = cartItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}
