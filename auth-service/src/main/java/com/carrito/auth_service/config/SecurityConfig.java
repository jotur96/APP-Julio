package com.carrito.auth_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // deshabilita CSRF con el nuevo estilo de lambdas
                .csrf(AbstractHttpConfigurer::disable)

                // reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // endpoints públicos
                        .requestMatchers(
                                "/register",
                                "/login",
                                "/ping",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        // todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // HTTP Basic (temporal, hasta que implementes JWT)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}