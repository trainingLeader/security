package com.bkseducate.securityapp.adapters.out.persistence;

import com.bkseducate.securityapp.domain.model.Role;
import com.bkseducate.securityapp.domain.ports.RoleRepository;
import com.bkseducate.securityapp.infrastructure.persistence.entity.RoleEntity;
import com.bkseducate.securityapp.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador que conecta el puerto RoleRepository con la implementación JPA
 */
@Component
public class RoleRepositoryAdapter implements RoleRepository {
    
    private final RoleJpaRepository jpaRepository;
    
    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Role save(Role role) {
        RoleEntity entity = toEntity(role);
        RoleEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name)
            .map(this::toDomain);
    }
    
    @Override
    public Optional<Role> findByAuthority(String authority) {
        return jpaRepository.findByAuthority(authority)
            .map(this::toDomain);
    }
    
    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(this::toDomain);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
    
    @Override
    public boolean existsByAuthority(String authority) {
        return jpaRepository.existsByAuthority(authority);
    }
    
    private RoleEntity toEntity(Role role) {
        return new RoleEntity(
            role.getId(),
            role.getName(),
            role.getAuthority()
        );
    }
    
    private Role toDomain(RoleEntity entity) {
        return Role.reconstruct(
            entity.getId(),
            entity.getName(),
            entity.getAuthority()
        );
    }
}
