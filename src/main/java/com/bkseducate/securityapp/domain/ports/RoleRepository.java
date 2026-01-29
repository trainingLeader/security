package com.bkseducate.securityapp.domain.ports;

import com.bkseducate.securityapp.domain.model.Role;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de roles
 */
public interface RoleRepository {
    
    /**
     * Guarda un rol
     */
    Role save(Role role);
    
    /**
     * Busca un rol por nombre
     */
    Optional<Role> findByName(String name);
    
    /**
     * Busca un rol por authority
     */
    Optional<Role> findByAuthority(String authority);
    
    /**
     * Busca un rol por ID
     */
    Optional<Role> findById(UUID id);
    
    /**
     * Verifica si existe un rol con el nombre dado
     */
    boolean existsByName(String name);
    
    /**
     * Verifica si existe un rol con el authority dado
     */
    boolean existsByAuthority(String authority);
}
