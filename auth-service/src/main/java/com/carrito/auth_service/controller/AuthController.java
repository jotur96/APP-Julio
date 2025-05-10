package com.carrito.auth_service.controller;

import com.carrito.auth_service.dto.*;
import com.carrito.auth_service.model.User;
import com.carrito.auth_service.repository.UserRepository;
import com.carrito.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    private final UserService service;
    private final UserRepository userRepository;

    public AuthController(UserService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        User u = service.register(req);
        RegisterResponse resp = new RegisterResponse(u.getId(), u.getEmail());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = service.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }


    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication auth) {
        User u = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UserProfileResponse profile = new UserProfileResponse(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getAddress(),
                u.getBirthDate()
        );
        return ResponseEntity.ok(profile);
    }
}
