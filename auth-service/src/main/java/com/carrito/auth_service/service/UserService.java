package com.carrito.auth_service.service;


import com.carrito.auth_service.dto.RegisterRequest;
import com.carrito.auth_service.model.User;
import com.carrito.auth_service.repository.UserRepository;
import com.carrito.auth_service.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public UserService(UserRepository repo, JwtUtil jwtUtil, AuthenticationConfiguration authConfig) throws Exception{
        this.repo = repo;
        this.encoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
        this.authManager = authConfig.getAuthenticationManager();
    }

    public User register(RegisterRequest req) {
        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        User u = new User();
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setAddress(req.getAddress());
        u.setBirthDate(req.getBirthDate());
        return repo.save(u);
    }


    public String login(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email y contraseña son requeridos");
        }
        if (email.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Email y contraseña no pueden estar vacíos");
        }
        if (!repo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email no registrado");
        }
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email no registrado"));


        return jwtUtil.generateToken(user);
    }
}