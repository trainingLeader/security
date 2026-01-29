package com.bkseducate.securityapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad JPA para Role
 */
@Entity
@Table(name = "roles")
public class RoleEntity {
    
    @Id
    @Column(columnDefinition = "CHAR(36)", length = 36)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String authority;
    
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();
    
    // Constructores
    public RoleEntity() {
    }
    
    public RoleEntity(UUID id, String name, String authority) {
        this.id = id;
        this.name = name;
        this.authority = authority;
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAuthority() {
        return authority;
    }
    
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    public Set<UserEntity> getUsers() {
        return users;
    }
    
    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
}
