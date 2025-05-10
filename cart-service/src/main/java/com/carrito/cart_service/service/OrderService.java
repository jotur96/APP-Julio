package com.carrito.cart_service.service;

import com.carrito.cart_service.dto.ProductDTO;
import com.carrito.cart_service.model.CartItem;
import com.carrito.cart_service.model.Order;
import com.carrito.cart_service.model.OrderItem;
import com.carrito.cart_service.repository.CartItemRepository;
import com.carrito.cart_service.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;
    private final WebClient productClient;

    public OrderService(
            CartItemRepository cartRepo,
            OrderRepository orderRepo,
            WebClient.Builder webClientBuilder,
            @Value("${product.service.url}") String productServiceUrl
    ) {
        this.cartRepo    = cartRepo;
        this.orderRepo   = orderRepo;
        this.productClient = webClientBuilder.baseUrl(productServiceUrl).build();
    }

    @Transactional
    public Long checkout(Long userId) {
        List<CartItem> cart = cartRepo.findByUserId(userId);
        if (cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "El carrito está vacío");
        }


        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(Instant.now());


        for (CartItem ci : cart) {

            BigDecimal price = Objects.requireNonNull(productClient.get()
                            .uri("/{id}", ci.getProductId())
                            .retrieve()
                            .bodyToMono(ProductDTO.class)
                            .block())
                    .price();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(price);

            order.getItems().add(oi);
        }

        Order saved = orderRepo.save(order);

        cartRepo.deleteByUserId(userId);

        return saved.getId();
    }

    public Order findById(Long orderId, Long userId) {
        return orderRepo.findById(orderId)
                .filter(o -> o.getUserId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }
}

