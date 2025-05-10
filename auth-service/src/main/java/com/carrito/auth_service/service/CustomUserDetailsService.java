package com.carrito.auth_service.service;

import com.carrito.auth_service.model.User;
import com.carrito.auth_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe"));

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(u.getEmail())
                .password(u.getPassword())
                .authorities("USER")
                .build();
    }
}
