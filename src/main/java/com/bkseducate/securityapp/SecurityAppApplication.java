package com.bkseducate.securityapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación SecurityApp
 * Arquitectura Hexagonal con Spring Boot 3.3.0 y Spring Security 6
 */
@SpringBootApplication
public class SecurityAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityAppApplication.class, args);
    }
}
