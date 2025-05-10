package com.carrito.auth_service.controller;

import com.carrito.auth_service.dto.RegisterRequest;
import com.carrito.auth_service.dto.RegisterResponse;
import com.carrito.auth_service.model.User;
import com.carrito.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User u = service.register(req);
        RegisterResponse resp = new RegisterResponse(u.getId(), u.getEmail());
        return ResponseEntity.ok(resp);
    }
}
