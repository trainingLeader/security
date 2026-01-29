package com.bkseducate.securityapp.infrastructure.persistence.repository;

import com.bkseducate.securityapp.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para RefreshTokenEntity
 */
@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    
    Optional<RefreshTokenEntity> findByToken(String token);
    
    List<RefreshTokenEntity> findByUserId(UUID userId);
    
    void deleteByUserId(UUID userId);
}
