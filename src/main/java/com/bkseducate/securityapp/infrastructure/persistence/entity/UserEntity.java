package com.bkseducate.securityapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.bkseducate.securityapp.domain.model.UserStatus;

/**
 * Entidad JPA para User
 * Esta es la representación de persistencia, separada del modelo de dominio
 */
@Entity
@Table(name = "users")
public class UserEntity {
    
    @Id
    @Column(columnDefinition = "CHAR(36)", length = 36)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    
    // Constructores
    public UserEntity() {
    }
    
    public UserEntity(UUID id, String email, String password, 
                     Set<RoleEntity> roles,
                     UserStatus status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.status = status;
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<RoleEntity> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
