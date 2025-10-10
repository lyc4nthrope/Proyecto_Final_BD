-- ============================================
-- DATOS INICIALES
-- ============================================

-- Insertar categorías según el proyecto
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Audio', 0.16, 0.35);
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Video', 0.19, 0.39);
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Tecnología', 0.12, 0.40);
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Cocina', 0.12, 0.35);

-- Insertar usuario administrador inicial
INSERT INTO USUARIO (username, password, nivel, estado) 
VALUES ('admin', 'admin123', 'Administrador', 'Activo');

-- Insertar vendedor de ejemplo
INSERT INTO VENDEDOR (nombre, telefono, correo)
VALUES ('Carlos Mendoza', '3001234567', 'carlos@empresa.com');

COMMIT;