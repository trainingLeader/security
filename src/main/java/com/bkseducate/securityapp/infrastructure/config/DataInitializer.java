package com.bkseducate.securityapp.infrastructure.config;

import com.bkseducate.securityapp.domain.model.Role;
import com.bkseducate.securityapp.domain.ports.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Inicializador de datos para roles
 * Crea los roles iniciales si no existen en la base de datos
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final RoleRepository roleRepository;
    
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void run(String... args) {
        initializeRoles();
    }
    
    private void initializeRoles() {
        logger.info("Inicializando roles...");
        
        // Crear rol USER
        if (!roleRepository.existsByAuthority("ROLE_USER")) {
            Role userRole = Role.reconstruct(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                "USER",
                "ROLE_USER"
            );
            roleRepository.save(userRole);
            logger.info("Rol USER creado");
        }
        
        // Crear rol ADMIN
        if (!roleRepository.existsByAuthority("ROLE_ADMIN")) {
            Role adminRole = Role.reconstruct(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                "ADMIN",
                "ROLE_ADMIN"
            );
            roleRepository.save(adminRole);
            logger.info("Rol ADMIN creado");
        }
        
        // Crear rol MODERATOR
        if (!roleRepository.existsByAuthority("ROLE_MODERATOR")) {
            Role moderatorRole = Role.reconstruct(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440003"),
                "MODERATOR",
                "ROLE_MODERATOR"
            );
            roleRepository.save(moderatorRole);
            logger.info("Rol MODERATOR creado");
        }
        
        logger.info("Inicialización de roles completada");
    }
}
