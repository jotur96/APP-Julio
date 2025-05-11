    package com.carrito.product_service.model;

    import jakarta.persistence.*;
    import java.math.BigDecimal;

    @Entity
    @Table(name = "products")
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String description;
        private BigDecimal price;
        private String imageUrl;

        public Product() {}

        public Product(String name, String description, BigDecimal price, String imageUrl) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.imageUrl = imageUrl;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }