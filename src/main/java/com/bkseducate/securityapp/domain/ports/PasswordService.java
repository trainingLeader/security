package com.bkseducate.securityapp.domain.ports;

/**
 * Puerto de salida para servicios de contraseña
 * El dominio necesita hashear y verificar contraseñas sin conocer la implementación
 */
public interface PasswordService {
    
    /**
     * Hashea una contraseña en texto plano
     */
    String hash(String plainPassword);
    
    /**
     * Verifica si una contraseña en texto plano coincide con el hash
     */
    boolean matches(String plainPassword, String hashedPassword);
}
