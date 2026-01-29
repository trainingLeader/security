package com.bkseducate.securityapp.adapters.out.persistence;

import com.bkseducate.securityapp.domain.model.RefreshToken;
import com.bkseducate.securityapp.domain.ports.RefreshTokenRepository;
import com.bkseducate.securityapp.infrastructure.persistence.entity.RefreshTokenEntity;
import com.bkseducate.securityapp.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador que conecta el puerto RefreshTokenRepository con la implementación JPA
 */
@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    
    private final RefreshTokenJpaRepository jpaRepository;
    
    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
            .map(this::toDomain);
    }
    
    @Override
    public List<RefreshToken> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(this::toDomain)
            .toList();
    }
    
    @Override
    public void delete(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        jpaRepository.delete(entity);
    }
    
    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }
    
    private RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        return new RefreshTokenEntity(
            refreshToken.getId(),
            refreshToken.getUserId(),
            refreshToken.getToken(),
            refreshToken.getExpiresAt(),
            refreshToken.isRevoked()
        );
    }
    
    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.reconstruct(
            entity.getId(),
            entity.getUserId(),
            entity.getToken(),
            entity.getExpiresAt(),
            entity.isRevoked()
        );
    }
}
