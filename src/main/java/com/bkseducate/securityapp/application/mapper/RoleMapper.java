package com.bkseducate.securityapp.application.mapper;

import com.bkseducate.securityapp.domain.model.Role;
import org.springframework.stereotype.Component;

/**
 * Utilidad para conversión de Role
 * Clase utilitaria para conversiones de Role a String
 */
@Component
public class RoleMapper {
    
    /**
     * Convierte un Role a String (nombre)
     */
    public String roleToString(Role role) {
        return role != null ? role.getName() : null;
    }
    
    /**
     * Convierte un Role a String (authority)
     */
    public String roleToAuthority(Role role) {
        return role != null ? role.getAuthority() : null;
    }
}
