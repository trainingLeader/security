package com.bkseducate.securityapp.domain.exceptions;

/**
 * Excepción lanzada cuando un usuario no se encuentra
 */
public class UserNotFoundException extends DomainException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
}
