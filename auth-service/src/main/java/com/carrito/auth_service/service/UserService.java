package com.carrito.auth_service.service;


import com.carrito.auth_service.dto.RegisterRequest;
import com.carrito.auth_service.model.User;
import com.carrito.auth_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo) {
        this.repo = repo;
        this.encoder = new BCryptPasswordEncoder();
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
}