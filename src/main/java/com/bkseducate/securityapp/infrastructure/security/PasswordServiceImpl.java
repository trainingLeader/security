package com.bkseducate.securityapp.infrastructure.security;

import com.bkseducate.securityapp.domain.ports.PasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación del puerto PasswordService usando BCrypt
 * Esta es una implementación de infraestructura
 */
@Service
public class PasswordServiceImpl implements PasswordService {
    
    private final PasswordEncoder passwordEncoder;
    
    public PasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    @Override
    public boolean matches(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
