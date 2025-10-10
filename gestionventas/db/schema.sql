-- ============================================
-- SISTEMA DE VENTAS DE ELECTRODOMÉSTICOS
-- Base de Datos Oracle - Esquema
-- ============================================

-- ============================================
-- 1. TABLA USUARIO
-- ============================================
CREATE TABLE USUARIO (
    id_usuario NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    nivel VARCHAR2(20) NOT NULL CHECK (nivel IN ('Administrador', 'Parametrico', 'Esporadico')),
    estado VARCHAR2(10) DEFAULT 'Activo' CHECK (estado IN ('Activo', 'Inactivo'))
);

-- ============================================
-- 2. TABLA AUDITORIA
-- ============================================
CREATE TABLE AUDITORIA (
    id_auditoria NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario NUMBER NOT NULL,
    accion VARCHAR2(20) NOT NULL CHECK (accion IN ('Login', 'Logout')),
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);

-- ============================================
-- 3. TABLA VENDEDOR
-- ============================================
CREATE TABLE VENDEDOR (
    id_vendedor NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    telefono VARCHAR2(20),
    correo VARCHAR2(100)
);

-- ============================================
-- 4. TABLA CATEGORIA
-- ============================================
CREATE TABLE CATEGORIA (
    id_categoria NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR2(50) NOT NULL UNIQUE,
    iva NUMBER(4,2) NOT NULL,
    utilidad NUMBER(4,2) NOT NULL
);

-- ============================================
-- 5. TABLA PRODUCTO
-- ============================================
CREATE TABLE PRODUCTO (
    id_producto NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR2(20) NOT NULL UNIQUE,
    nombre VARCHAR2(100) NOT NULL,
    id_categoria NUMBER NOT NULL,
    valor_adquisicion NUMBER(12,2) NOT NULL,
    valor_venta NUMBER(12,2) NOT NULL,
    stock NUMBER DEFAULT 0 NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES CATEGORIA(id_categoria)
);

-- ============================================
-- 6. TABLA CLIENTE
-- ============================================
CREATE TABLE CLIENTE (
    id_cliente NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cedula VARCHAR2(20) NOT NULL UNIQUE,
    nombre VARCHAR2(100) NOT NULL,
    direccion VARCHAR2(200),
    telefono VARCHAR2(20),
    correo VARCHAR2(100),
    tiene_credito_activo NUMBER(1) DEFAULT 0,
    CONSTRAINT chk_credito_activo CHECK (tiene_credito_activo IN (0, 1))
);

-- ============================================
-- 7. TABLA VENTA
-- ============================================
CREATE TABLE VENTA (
    id_venta NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_venta VARCHAR2(10) NOT NULL CHECK (tipo_venta IN ('Contado', 'Credito')),
    fecha DATE NOT NULL,
    id_cliente NUMBER NOT NULL,
    id_vendedor NUMBER NOT NULL,
    id_usuario NUMBER NOT NULL,
    subtotal NUMBER(12,2) NOT NULL,
    total_iva NUMBER(12,2) NOT NULL,
    total NUMBER(12,2) NOT NULL,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente),
    CONSTRAINT fk_venta_vendedor FOREIGN KEY (id_vendedor) REFERENCES VENDEDOR(id_vendedor),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);

-- ============================================
-- 8. TABLA DETALLE_VENTA
-- ============================================
CREATE TABLE DETALLE_VENTA (
    id_detalle_venta NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta NUMBER NOT NULL,
    id_producto NUMBER NOT NULL,
    cantidad NUMBER NOT NULL,
    precio_unitario NUMBER(12,2) NOT NULL,
    iva NUMBER(4,2) NOT NULL,
    subtotal NUMBER(12,2) NOT NULL,
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES VENTA(id_venta),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES PRODUCTO(id_producto)
);

-- ============================================
-- 9. TABLA VENTA_CONTADO
-- ============================================
CREATE TABLE VENTA_CONTADO (
    id_venta_contado NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta NUMBER NOT NULL UNIQUE,
    monto_pagado NUMBER(12,2) NOT NULL,
    CONSTRAINT fk_venta_contado FOREIGN KEY (id_venta) REFERENCES VENTA(id_venta)
);

-- ============================================
-- 10. TABLA VENTA_CREDITO
-- ============================================
CREATE TABLE VENTA_CREDITO (
    id_venta_credito NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta NUMBER NOT NULL UNIQUE,
    cuota_inicial NUMBER(12,2) NOT NULL,
    saldo_financiado NUMBER(12,2) NOT NULL,
    interes_aplicado NUMBER(4,2) DEFAULT 0.05 NOT NULL,
    num_cuotas NUMBER NOT NULL CHECK (num_cuotas IN (12, 18, 24)),
    valor_cuota NUMBER(12,2) NOT NULL,
    saldo_pendiente NUMBER(12,2) NOT NULL,
    fecha_limite_pago DATE,
    CONSTRAINT fk_venta_credito FOREIGN KEY (id_venta) REFERENCES VENTA(id_venta)
);

-- ============================================
-- 11. TABLA CUOTA
-- ============================================
CREATE TABLE CUOTA (
    id_cuota NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta_credito NUMBER NOT NULL,
    num_cuota NUMBER NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    fecha_pago DATE,
    valor_cuota NUMBER(12,2) NOT NULL,
    estado VARCHAR2(20) DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente', 'Pagada', 'Vencida')),
    CONSTRAINT uk_cuota_venta UNIQUE (id_venta_credito, num_cuota),
    CONSTRAINT fk_cuota_venta_credito FOREIGN KEY (id_venta_credito) REFERENCES VENTA_CREDITO(id_venta_credito)
);


COMMIT;