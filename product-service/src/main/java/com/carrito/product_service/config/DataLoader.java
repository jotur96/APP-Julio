package com.carrito.product_service.config;

import com.carrito.product_service.model.Product;
import com.carrito.product_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadInitialData(ProductRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Product(
                        "Pelota de fútbol",
                        "Pelota oficial tamaño 5",
                        new BigDecimal("29.99"),
                        "https://editorial.uefa.com/resources/025a-0ea944223e16-151ee2d5b032-1000/1914526.jpeg"
                ));
                repo.save(new Product(
                        "Raqueta de tenis",
                        "Raqueta ligera de carbono",
                        new BigDecimal("89.50"),
                        "https://wilsonstore.cl/cdn/shop/files/WR149811U_0_Blade_98_16x19_V9_GR.png.high-res.jpg"
                ));
                repo.save(new Product(
                        "Guantes de boxeo",
                        "Guantes profesionales de entrenamiento",
                        new BigDecimal("45.00"),
                        "https://cellshop.com.py/media/catalog/product/4/3/4349839_1_1.jpg"
                ));
            }
        };
    }
}
