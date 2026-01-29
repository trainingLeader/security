package com.bkseducate.securityapp.infrastructure.exception;

import java.time.LocalDateTime;

/**
 * Formato estándar de respuesta de error HTTP
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status,
            error,
            message,
            path
        );
    }
}
