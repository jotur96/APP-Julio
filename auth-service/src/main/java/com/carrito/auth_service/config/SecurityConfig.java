package com.carrito.auth_service.config;

import com.carrito.auth_service.security.JwtAuthenticationFilter;
import com.carrito.auth_service.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // lambdas style para deshabilitar CSRF
                .csrf(csrf -> csrf.disable())

                // tenemos un filtro JWT que quita el Bearer y carga el contexto
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(auth -> auth
                        // internamente el gateway strippea /api/auth → llega como "/register" y "/login"
                        .requestMatchers(
                                "/register",
                                "/login",
                                "/ping",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        // "/me" y cualquier otra ruta exigen JWT
                        .anyRequest().authenticated()
                )

                // quitamos httpBasic para evitar que pida realm Basic
                .httpBasic(AbstractHttpConfigurer::disable);
        ;

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
