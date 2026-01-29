package com.bkseducate.securityapp.domain.model;

import com.bkseducate.securityapp.domain.exceptions.DomainException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad de dominio User
 * No tiene dependencias de frameworks externos
 */
public class User {
    
    private UUID id;
    private String email;
    private String password;
    private Set<Role> roles;
    private UserStatus status;
    
    // Constructor privado para forzar uso de factory methods
    private User() {
        this.roles = new HashSet<>();
    }
    
    /**
     * Factory method para crear un nuevo usuario
     */
    public static User create(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new DomainException("Email no puede estar vacío");
        }
        if (password == null || password.isBlank()) {
            throw new DomainException("Password no puede estar vacío");
        }
        if (!isValidEmail(email)) {
            throw new DomainException("Email inválido");
        }
        
        User user = new User();
        user.id = UUID.randomUUID();
        user.email = email.toLowerCase().trim();
        user.password = password; // Se espera que venga hasheado desde fuera del dominio
        user.status = UserStatus.ACTIVE;
        // El rol USER se asignará desde el caso de uso usando RoleRepository
        
        return user;
    }
    
    /**
     * Factory method para reconstruir desde persistencia
     */
    public static User reconstruct(UUID id, String email, String password, 
                                   Set<Role> roles, UserStatus status) {
        User user = new User();
        user.id = id;
        user.email = email;
        user.password = password;
        user.roles = new HashSet<>(roles);
        user.status = status;
        return user;
    }
    
    /**
     * Asigna un rol al usuario
     */
    public void assignRole(Role role) {
        if (role == null) {
            throw new DomainException("El rol no puede ser nulo");
        }
        this.roles.add(role);
    }
    
    /**
     * Remueve un rol del usuario
     */
    public void removeRole(Role role) {
        if (role == null) {
            throw new DomainException("El rol no puede ser nulo");
        }
        this.roles.remove(role);
    }
    
    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
    
    /**
     * Cambia la contraseña del usuario
     */
    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new DomainException("La nueva contraseña no puede estar vacía");
        }
        this.password = newPassword; // Se espera que venga hasheada
    }
    
    /**
     * Bloquea el usuario
     */
    public void block() {
        this.status = UserStatus.BLOCKED;
    }
    
    /**
     * Activa el usuario
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }
    
    /**
     * Verifica si el usuario está activo
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
    
    // Validación simple de email
    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public Set<Role> getRoles() {
        return new HashSet<>(roles); // Retorna copia para inmutabilidad
    }
    
    public UserStatus getStatus() {
        return status;
    }
}
