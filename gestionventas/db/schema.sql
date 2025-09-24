--Aquí defines tablas, relaciones, 
--índices, secuencias, constraints (PRIMARY KEY, FOREIGN KEY, UNIQUE, etc.).

--Básicamente: la estructura de la base de datos.


CREATE TABLE ROL (
    id_rol NUMBER PRIMARY KEY,
    nombre VARCHAR2(30) NOT NULL,
    descripcion VARCHAR2(100)
);

CREATE TABLE USUARIO (
    id_usuario NUMBER PRIMARY KEY,
    password_hash VARCHAR2(100) NOT NULL,
    nombre VARCHAR2(100) NOT NULL,
    id_rol NUMBER NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES ROL(id_rol)
);

CREATE TABLE CATEGORIA (
    id_categoria NUMBER PRIMARY KEY,
    nombre VARCHAR2(50) NOT NULL,
    iva DECIMAL(5,2) NOT NULL,
    utilidad_pct DECIMAL(5,2) NOT NULL
);

CREATE TABLE PRODUCTO (
    id_producto NUMBER PRIMARY KEY,
    codigo VARCHAR2(50),
    nombre VARCHAR2(100) NOT NULL,
    valor_adquisicion DECIMAL(10,2) NOT NULL,
    valor_venta DECIMAL(10,2) NOT NULL,
    stock NUMBER NOT NULL,
    id_categoria NUMBER NOT NULL,
    activo NUMBER(1) DEFAULT 1,
    FOREIGN KEY (id_categoria) REFERENCES CATEGORIA(id_categoria)
);

CREATE TABLE CLIENTE (
    id_cliente NUMBER PRIMARY KEY,
    cedula VARCHAR2(20) UNIQUE NOT NULL,
    nombre VARCHAR2(100) NOT NULL,
    direccion VARCHAR2(150),
    telefono VARCHAR2(20),
    correo VARCHAR2(100)
);

CREATE TABLE VENTA (
    id_venta NUMBER PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo_venta VARCHAR2(10) CHECK (tipo_venta IN ('CONTADO','CREDITO')),
    id_cliente NUMBER NOT NULL,
    id_usuario NUMBER NOT NULL,
    entrada DECIMAL(10,2),
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR2(10) CHECK (estado IN ('ACTIVA','PAGADA','ANULADA')),
    FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);

CREATE TABLE DETALLE_VENTA (
    id_detalle NUMBER PRIMARY KEY,
    id_venta NUMBER NOT NULL,
    id_producto NUMBER NOT NULL,
    cantidad NUMBER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES VENTA(id_venta),
    FOREIGN KEY (id_producto) REFERENCES PRODUCTO(id_producto)
);

CREATE TABLE CUOTA (
    id_cuota NUMBER PRIMARY KEY,
    id_venta NUMBER NOT NULL,
    numero_cuota NUMBER NOT NULL,
    valor_cuota DECIMAL(10,2) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    fecha_pago DATE,
    estado VARCHAR2(10) CHECK (estado IN ('PENDIENTE','PAGADA','VENCIDA')),
    FOREIGN KEY (id_venta) REFERENCES VENTA(id_venta)
);

CREATE TABLE PAGO (
    id_pago NUMBER PRIMARY KEY,
    id_venta NUMBER NOT NULL,
    id_cuota NUMBER,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    id_usuario NUMBER NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES VENTA(id_venta),
    FOREIGN KEY (id_cuota) REFERENCES CUOTA(id_cuota),
    FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);