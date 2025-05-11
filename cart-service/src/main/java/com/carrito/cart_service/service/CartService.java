package com.carrito.cart_service.service;

import com.carrito.cart_service.dto.CartItemResponse;
import com.carrito.cart_service.dto.ProductDTO;
import com.carrito.cart_service.model.CartItem;
import com.carrito.cart_service.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private final CartItemRepository repo;
    private final WebClient productClient;

    public CartService(
            CartItemRepository repo,
            WebClient.Builder webClientBuilder,
            @Value("${product.service.url:http://gateway:8080/api/products}") String productServiceUrl
    ) {
        this.repo = repo;
        this.productClient = webClientBuilder
                .baseUrl(productServiceUrl)
                .build();
    }

    public List<CartItemResponse> listItems(Long userId) {
        List<CartItemResponse> cart = new ArrayList<>();
        List<CartItem> items = repo.findByUserId(userId);
        for (CartItem item : items) {
            ProductDTO dto = productClient.get()
                    .uri("/{id}", item.getProductId())
                    .retrieve()
                    .onStatus(s -> s.value() == 404,
                            resp -> Mono.error(new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Producto no existe: " + item.getProductId())))
                    .bodyToMono(ProductDTO.class)    // ← aquí sí descargas el cuerpo
                    .block();
            CartItemResponse cartItem = new CartItemResponse();
            cartItem.setProduct(dto);
            cartItem.setQuantity(item.getQuantity());
            cart.add(cartItem);
        }
        return cart;
    }

    @Transactional
    public CartItem addOrUpdateItem(Long userId, Long productId, int quantity) {
        productClient.get()
                .uri("/{id}", productId)
                .retrieve()
                .onStatus(s -> s.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Producto no existe: " + productId)))
                .toBodilessEntity()
                .block();

        CartItem item = repo.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> new CartItem(userId, productId, 0));

        if (quantity <= 0) {
            if (item.getId() != null) repo.delete(item);
            return null;
        }
        item.setQuantity(quantity);
        return repo.save(item);
    }

    @Transactional
    public void removeItem(Long userId, Long productId) {
        repo.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        repo.deleteByUserId(userId);
    }
}
