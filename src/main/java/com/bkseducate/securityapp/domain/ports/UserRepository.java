package com.bkseducate.securityapp.domain.ports;

import com.bkseducate.securityapp.domain.model.User;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de usuarios
 * Define las operaciones que el dominio necesita sin especificar implementación
 */
public interface UserRepository {
    
    /**
     * Guarda un usuario
     */
    User save(User user);
    
    /**
     * Busca un usuario por email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Busca un usuario por ID
     */
    Optional<User> findById(UUID id);
    
    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Elimina un usuario
     */
    void delete(User user);
}
