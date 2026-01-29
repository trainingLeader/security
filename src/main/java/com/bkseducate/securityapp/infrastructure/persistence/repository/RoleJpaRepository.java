package com.bkseducate.securityapp.infrastructure.persistence.repository;

import com.bkseducate.securityapp.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para RoleEntity
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    
    Optional<RoleEntity> findByName(String name);
    
    Optional<RoleEntity> findByAuthority(String authority);
    
    boolean existsByName(String name);
    
    boolean existsByAuthority(String authority);
}
