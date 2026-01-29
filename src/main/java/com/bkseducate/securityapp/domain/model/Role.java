package com.bkseducate.securityapp.domain.model;

import com.bkseducate.securityapp.domain.exceptions.DomainException;
import java.util.UUID;

/**
 * Entidad de dominio Role
 * Representa un rol del sistema
 */
public class Role {
    
    private UUID id;
    private String name;
    private String authority;
    
    // Constructor privado para forzar uso de factory methods
    private Role() {
    }
    
    /**
     * Factory method para crear un nuevo rol
     */
    public static Role create(String name, String authority) {
        if (name == null || name.isBlank()) {
            throw new DomainException("El nombre del rol no puede estar vacío");
        }
        if (authority == null || authority.isBlank()) {
            throw new DomainException("El authority del rol no puede estar vacío");
        }
        
        Role role = new Role();
        role.id = UUID.randomUUID();
        role.name = name.toUpperCase().trim();
        role.authority = authority.toUpperCase().trim();
        
        return role;
    }
    
    /**
     * Factory method para reconstruir desde persistencia
     */
    public static Role reconstruct(UUID id, String name, String authority) {
        Role role = new Role();
        role.id = id;
        role.name = name;
        role.authority = authority;
        return role;
    }
    
    /**
     * Verifica si el rol tiene un authority específico
     */
    public boolean hasAuthority(String authority) {
        return this.authority.equalsIgnoreCase(authority);
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAuthority() {
        return authority;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
