package com.bkseducate.securityapp.application.mapper;

import com.bkseducate.securityapp.application.dto.RoleResponse;
import com.bkseducate.securityapp.application.dto.UserResponse;
import com.bkseducate.securityapp.domain.model.Role;
import com.bkseducate.securityapp.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * Mapper MapStruct para conversión entre User (dominio) y UserResponse (DTO)
 * 
 * Con componentModel = "spring", se inyecta como bean de Spring.
 * No usar INSTANCE estático con este modelo.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    /**
     * Convierte un User del dominio a UserResponse DTO
     */
    @Mapping(target = "roles", source = "roles")
    UserResponse toResponse(User user);
    
    /**
     * Convierte un Role del dominio a RoleResponse DTO
     */
    RoleResponse toRoleResponse(Role role);
}
