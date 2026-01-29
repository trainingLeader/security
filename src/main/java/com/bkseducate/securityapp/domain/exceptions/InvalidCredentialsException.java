package com.bkseducate.securityapp.domain.exceptions;

/**
 * Excepción lanzada cuando las credenciales son inválidas
 */
public class InvalidCredentialsException extends DomainException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
