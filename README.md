# Prueba Técnica – Carrito de Compras (Microservicios)

Este repositorio contiene una solución de **Carrito de Compras** basada en arquitectura de microservicios, desarrollada con Java Spring Boot y orquestada con Docker Compose.

---

## 🏗 Arquitectura

- **product-service** (puerto 8081)  (interno)
  Servicio de productos (nombre, descripción, precio, stock).
- **cart-service** (puerto 8082) (interno) 
  Gestión de carritos de usuario: creación, añadir/quitar ítems, checkout de ordenes.
- **auth-service** (puerto 8083)  (interno)
  Registro, login y emisión de JWT.
- **api-gateway** (puerto 8080)  (expuesto)
  Spring Cloud Gateway que enruta peticiones a cada microservicio y valida JWT.

---

## 🚀 Tecnologías utilizadas

- **Java 21** con **Spring Boot 3.x**  
- **Spring Data JPA** + **Hibernate**  
- **Spring Security** y **JWT**  
- **Spring Cloud Gateway**  
- **Maven 3.9**  
- **Base de datos MariaDB**
- **Docker** & **Docker Compose**  
- **Lombok**  

---

## 📋 Requisitos previos

- Docker & Docker Compose  
- (Opcional) JDK 21 y Maven para ejecución local sin contenedores  

---

## ⚙️ Levantar los servicios

1. **Clonar el repositorio**  
   ```bash
   git clone https://github.com/jotur96/APP-Julio.git
   cd APP-Julio

3. **Levantar los servicios del backend**
   ```bash
   docker compose up --build -d

4. **Levantar el frontend**
   ```bash
   cd frontend/
   npm run dev
