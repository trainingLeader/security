-- Script de inicialización de roles
-- Este script se ejecuta automáticamente al iniciar la aplicación

-- Insertar roles iniciales si no existen
-- Para MySQL con UUID como CHAR(36)
INSERT IGNORE INTO roles (id, name, authority) 
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'USER', 'ROLE_USER'),
    ('550e8400-e29b-41d4-a716-446655440002', 'ADMIN', 'ROLE_ADMIN'),
    ('550e8400-e29b-41d4-a716-446655440003', 'MODERATOR', 'ROLE_MODERATOR');
