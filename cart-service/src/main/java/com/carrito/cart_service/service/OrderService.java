package com.carrito.cart_service.service;

import com.carrito.cart_service.dto.CartItemResponse;
import com.carrito.cart_service.dto.OrdersResponse;
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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public List<OrdersResponse> findAllByUserId(Long userId) {
        List<OrdersResponse> orders = new ArrayList<>();
        List<Order> orderIds = orderRepo.findAllByUserId(userId);

        for (Order orderItem : orderIds) {
            Long orderId = orderItem.getId();
            OrdersResponse order = findById(orderId, userId);
            orders.add(order);
        }

        return orders;
    }

    public OrdersResponse findById(Long orderId, Long userId) {

        OrdersResponse orderResponse = new OrdersResponse();
        List<CartItemResponse> cartItems = new ArrayList<>();

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        orderResponse.setId(order.getId());
        orderResponse.setCreatedAt(order.getCreatedAt().toString());

        for (OrderItem oi : order.getItems()) {
            CartItemResponse cartItem = new CartItemResponse();

            ProductDTO dto = productClient.get()
                    .uri("/{id}", oi.getProductId())
                    .retrieve()
                    .onStatus(s -> s.value() == 404,
                            resp -> Mono.error(new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Producto no existe: " + oi.getProductId())))
                    .bodyToMono(ProductDTO.class)    // ← aquí sí descargas el cuerpo
                    .block();

            cartItem.setProduct(dto);
            cartItem.setQuantity(oi.getQuantity());
            cartItems.add(cartItem);
        }

        orderResponse.setItems(cartItems);

        return orderResponse;
    }
}

