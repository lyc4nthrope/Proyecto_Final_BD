-- ============================================
-- DATOS INICIALES
-- ============================================

-- Insertar categorías según el proyecto
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Audio', 0.16, 0.35);
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Video', 0.19, 0.39);
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Tecnología', 0.12, 0.40);
INSERT INTO CATEGORIA (nombre, iva, utilidad) VALUES ('Cocina', 0.12, 0.35);

COMMIT;

-- ============================================
-- DATOS DE PRUEBA
-- ============================================
-- ============================================
-- 1. TABLA VENDEDOR
-- ============================================
INSERT INTO VENDEDOR (nombre, telefono, correo) VALUES ('Carlos Ramírez', '3101234567', 'carlos.ramirez@electro.com');
INSERT INTO VENDEDOR (nombre, telefono, correo) VALUES ('Ana Gutierrez', '3119876543', 'ana.gutierrez@electro.com');
INSERT INTO VENDEDOR (nombre, telefono, correo) VALUES ('Pedro Pascal', '3205551212', 'pedro.pascal@electro.com');
INSERT INTO VENDEDOR (nombre, telefono, correo) VALUES ('Sofia Lopez', '3007654321', 'sofia.lopez@electro.com');
INSERT INTO VENDEDOR (nombre, telefono, correo) VALUES ('Carlos Mendoza', '3001234567', 'carlos@empresa.com');

-- ============================================
-- 2. TABLA PRODUCTO
-- ============================================
-- Los valores de venta están calculados como: valor_adquisicion * (1 + utilidad) * (1 + iva)
-- Cat 1: Audio
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('AUD-001', 'Equipo de sonido Sony', 1, 1500000, 2320500, 15);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('AUD-002', 'Microfono', 1, 1800000, 2784600, 10);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('AUD-003', 'Altavoces', 1, 900000, 1392300, 20);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('AUD-004', 'Barra de Sonido Dolby Atmos 5.1', 1, 1100000, 1701700, 18);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('AUD-005', 'Audífonos Bluetooth Noise Cancelling', 1, 700000, 1082900, 40);
-- Cat 2: Video
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('TV-001', 'Smart TV LED 55" 4K', 2, 2000000, 2975000, 25);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('TV-002', 'Smart TV QLED 65" 8K', 2, 4500000, 6693750, 5);
-- Cat 3: Tecnologia
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('COMP-001', 'Laptop Core i7 16GB RAM 1TB SSD', 3, 3200000, 5143200, 12);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('COMP-002', 'Monitor Gamer Curvo 32"', 3, 1300000, 2089650, 20);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('CEL-001', 'Smartphone Gama Alta 256GB', 3, 2800000, 4260160, 30);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('CEL-002', 'Smartphone Gama Media 128GB', 3, 1200000, 1827840, 50);
-- Cat 4: Cocina
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('PE-001', 'Licuadora XtremePower 10 Vel', 4, 150000, 249900, 50);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('PE-002', 'Freidora de Aire 5L Digital', 4, 350000, 583100, 30);
INSERT INTO PRODUCTO (codigo, nombre, id_categoria, valor_adquisicion, valor_venta, stock) VALUES ('PE-003', 'Cafetera Espresso Automática', 4, 800000, 1332800, 15);


-- ============================================
-- 3. TABLA CLIENTE
-- ============================================
INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES ('1090123456', 'Lucía Fernández', 'Calle 10 # 5-20', '3001112233', 'lucia.f@mail.com', 0);
INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES ('1090789012', 'Miguel Angel Torres', 'Av. Santander # 15-45', '3014445566', 'miguel.t@mail.com', 0);
INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES ('79123456', 'Ricardo Mendoza', 'Cra 22 # 50-10 Apt 301', '3157778899', 'ricardo.m@mail.com', 0);
INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES ('52789456', 'Sofia Vergara', 'Manzana 5 Casa 10 Barrio Sol', '3189990011', 'sofia.v@mail.com', 0);
INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES ('1010202030', 'Javier Peña', 'Km 2 Vía Aeropuerto', '3216549870', 'javier.p@mail.com', 0);
INSERT INTO CLIENTE (cedula, nombre, direccion, telefono, correo, tiene_credito_activo) VALUES ('1094567890', 'Elena Correa', 'Av. Bolivar # 30-N-10', '3145556677', 'elena.c@mail.com', 0);

-- ============================================
-- 4. TABLA VENTA
-- ============================================
-- Venta 1 (Contado): 1 TV 55" (ID 4), 2 Licuadoras (ID 8)
-- Subtotal = (2.000.000 * 1.25) + (150.000 * 1.40 * 2) = 2.500.000 + 420.000 = 2.920.000
-- Total IVA = (2.500.000 * 0.19) + (420.000 * 0.19) = 475.000 + 79.800 = 554.800
-- Total = 2.920.000 + 554.800 = 3.474.800
INSERT INTO VENTA (tipo_venta, fecha, id_cliente, id_vendedor, id_usuario, subtotal, total_iva, total) 
VALUES ('Contado', TO_DATE('2025-10-10', 'YYYY-MM-DD'), 1, 1, 2, 2920000, 554800, 3474800);

-- Venta 2 (Credito): 1 Nevera (ID 1), 1 Lavadora (ID 2)
-- Subtotal = (1.500.000 * 1.30) + (1.800.000 * 1.30) = 1.950.000 + 2.340.000 = 4.290.000
-- Total IVA = (1.950.000 * 0.19) + (2.340.000 * 0.19) = 370.500 + 444.600 = 815.100
-- Total = 4.290.000 + 815.100 = 5.105.100
INSERT INTO VENTA (tipo_venta, fecha, id_cliente, id_vendedor, id_usuario, subtotal, total_iva, total) 
VALUES ('Credito', TO_DATE('2025-10-12', 'YYYY-MM-DD'), 2, 2, 2, 4290000, 815100, 5105100);

-- Venta 3 (Contado): 1 Laptop (ID 6)
-- Subtotal = (3.200.000 * 1.35) = 4.320.000
-- Total IVA = (4.320.000 * 0.19) = 820.800
-- Total = 4.320.000 + 820.800 = 5.140.800 (Ajuste en cálculo de producto 6)
-- Recalculo Producto 6: 3.200.000 * 1.35 = 4.320.000 (Subtotal)
-- 4.320.000 * 1.19 = 5.140.800 (Valor Venta) -> OK. (Ajusto el INSERT de PRODUCTO 6)
UPDATE PRODUCTO SET valor_venta = 5140800 WHERE id_producto = 6;
INSERT INTO VENTA (tipo_venta, fecha, id_cliente, id_vendedor, id_usuario, subtotal, total_iva, total) 
VALUES ('Contado', TO_DATE('2025-10-15', 'YYYY-MM-DD'), 4, 1, 2, 4320000, 820800, 5140800);

-- Venta 4 (Credito): 1 Barra Sonido (ID 11), 1 Audífonos (ID 12), 1 Celular (ID 14)
-- Subtotal = (1.100.000 * 1.30) + (700.000 * 1.30) + (1.200.000 * 1.28) = 1.430.000 + 910.000 + 1.536.000 = 3.876.000
-- Total IVA = (3.876.000 * 0.19) = 736.440
-- Total = 3.876.000 + 736.440 = 4.612.440
INSERT INTO VENTA (tipo_venta, fecha, id_cliente, id_vendedor, id_usuario, subtotal, total_iva, total) 
VALUES ('Credito', TO_DATE('2025-10-20', 'YYYY-MM-DD'), 3, 3, 2, 3876000, 736440, 4612440);

-- Venta 5 (Contado): 1 Freidora (ID 9), 1 Cafetera (ID 10)
-- Subtotal = (350.000 * 1.40) + (800.000 * 1.40) = 490.000 + 1.120.000 = 1.610.000
-- Total IVA = (1.610.000 * 0.19) = 305.900
-- Total = 1.610.000 + 305.900 = 1.915.900
INSERT INTO VENTA (tipo_venta, fecha, id_cliente, id_vendedor, id_usuario, subtotal, total_iva, total) 
VALUES ('Contado', TO_DATE('2025-10-22', 'YYYY-MM-DD'), 5, 2, 1, 1610000, 305900, 1915900);

-- ============================================
-- 5. TABLA DETALLE_VENTA
-- ============================================
-- Venta 1 (ID=1)
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (1, 4, 1, 2975000, 0.19, 2500000);
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (1, 8, 2, 249900, 0.19, 420000);
-- Venta 2 (ID=2)
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (2, 1, 1, 2320500, 0.19, 1950000);
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (2, 2, 1, 2784600, 0.19, 2340000);
-- Venta 3 (ID=3)
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (3, 6, 1, 5140800, 0.19, 4320000);
-- Venta 4 (ID=4)
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (4, 11, 1, 1701700, 0.19, 1430000);
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (4, 12, 1, 1082900, 0.19, 910000);
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (4, 14, 1, 1827840, 0.19, 1536000);
-- Venta 5 (ID=5)
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (5, 9, 1, 583100, 0.19, 490000);
INSERT INTO DETALLE_VENTA (id_venta, id_producto, cantidad, precio_unitario, iva, subtotal) VALUES (5, 10, 1, 1332800, 0.19, 1120000);


-- ============================================
-- 6. TABLA VENTA_CONTADO
-- ============================================
-- Venta 1
INSERT INTO VENTA_CONTADO (id_venta, monto_pagado) VALUES (1, 3474800);
-- Venta 3
INSERT INTO VENTA_CONTADO (id_venta, monto_pagado) VALUES (3, 5140800);
-- Venta 5
INSERT INTO VENTA_CONTADO (id_venta, monto_pagado) VALUES (5, 1915900);

-- ============================================
-- 7. TABLA VENTA_CREDITO
-- ============================================
-- Venta 2 (ID=2). Total 5.105.100. Cuota Inicial 20% = 1.021.020. Saldo a financiar = 4.084.080
-- Cuotas: 24. Interés: 0.05 anual (0.0041667 mensual). Valor Cuota = 179.250
-- Saldo Pendiente (Total a pagar en cuotas) = 179.250 * 24 = 4.302.000
INSERT INTO VENTA_CREDITO (id_venta, cuota_inicial, saldo_financiado, interes_aplicado, num_cuotas, valor_cuota, saldo_pendiente, fecha_limite_pago)
VALUES (2, 1021020, 4084080, 0.05, 24, 179250, 4302000, ADD_MONTHS(TO_DATE('2025-10-12', 'YYYY-MM-DD'), 1));

-- Venta 4 (ID=4). Total 4.612.440. Cuota Inicial 30% = 1.383.732. Saldo a financiar = 3.228.708
-- Cuotas: 18. Interés: 0.05 anual. Valor Cuota = 187.730
-- Saldo Pendiente (Total a pagar en cuotas) = 187.730 * 18 = 3.379.140
INSERT INTO VENTA_CREDITO (id_venta, cuota_inicial, saldo_financiado, interes_aplicado, num_cuotas, valor_cuota, saldo_pendiente, fecha_limite_pago)
VALUES (4, 1383732, 3228708, 0.05, 18, 187730, 3379140, ADD_MONTHS(TO_DATE('2025-10-20', 'YYYY-MM-DD'), 1));

-- Actualizar estado de crédito de clientes
UPDATE CLIENTE SET tiene_credito_activo = 1 WHERE id_cliente = 2; -- Miguel Angel Torres
UPDATE CLIENTE SET tiene_credito_activo = 1 WHERE id_cliente = 3; -- Ricardo Mendoza

-- ============================================
-- 8. TABLA CUOTA
-- ============================================
-- Cuotas Venta Credito 1 (ID=1, que corresponde a Venta ID=2)
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (1, 1, ADD_MONTHS(TO_DATE('2025-10-12', 'YYYY-MM-DD'), 1), NULL, 179250, 'Pendiente');
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (1, 2, ADD_MONTHS(TO_DATE('2025-10-12', 'YYYY-MM-DD'), 2), NULL, 179250, 'Pendiente');
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (1, 3, ADD_MONTHS(TO_DATE('2025-10-12', 'YYYY-MM-DD'), 3), NULL, 179250, 'Pendiente');
-- Generar las 21 cuotas restantes (Opcional, solo para volumen)
BEGIN
    FOR i IN 4..24 LOOP
        INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
        VALUES (1, i, ADD_MONTHS(TO_DATE('2025-10-12', 'YYYY-MM-DD'), i), NULL, 179250, 'Pendiente');
    END LOOP;
END;
-- /

-- Cuotas Venta Credito 2 (ID=2, que corresponde a Venta ID=4)
-- Asumimos que la primera cuota ya está vencida y pagada
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (2, 1, ADD_MONTHS(TO_DATE('2025-10-20', 'YYYY-MM-DD'), 1), TO_DATE('2025-11-19', 'YYYY-MM-DD'), 187730, 'Pagada');
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (2, 2, ADD_MONTHS(TO_DATE('2025-10-20', 'YYYY-MM-DD'), 2), NULL, 187730, 'Pendiente');
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (2, 3, ADD_MONTHS(TO_DATE('2025-10-20', 'YYYY-MM-DD'), 3), NULL, 187730, 'Pendiente');
INSERT INTO CUOTA (id_venta_credito, num_cuota, fecha_vencimiento, fecha_pago, valor_cuota, estado)
VALUES (2, 4, ADD_MONTHS(TO_DATE('2025-10-20', 'YYYY-MM-DD'), 4), NULL, 187730, 'Pendiente');


COMMIT;