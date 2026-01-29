package com.bkseducate.securityapp.application.usecase;

import com.bkseducate.securityapp.application.dto.UserResponse;
import com.bkseducate.securityapp.application.mapper.UserMapper;
import com.bkseducate.securityapp.domain.exceptions.UserNotFoundException;
import com.bkseducate.securityapp.domain.model.User;
import com.bkseducate.securityapp.domain.ports.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Caso de uso: Obtener usuario autenticado
 */
@Service
public class GetCurrentUserUseCase {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    public GetCurrentUserUseCase(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    
    public UserResponse execute(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        return userMapper.toResponse(user);
    }
}
