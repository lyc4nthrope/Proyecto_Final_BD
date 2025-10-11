package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.*;
import com.uniquindio.crisdav.gestionventas.models.entity.*;
import com.uniquindio.crisdav.gestionventas.models.enums.TipoVenta;
import com.uniquindio.crisdav.gestionventas.models.vo.FacturaVO;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;
import com.uniquindio.crisdav.gestionventas.dao.ProductoDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaController {
    
    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final VentaContadoDAO ventaContadoDAO;
    private final VentaCreditoDAO ventaCreditoDAO;
    private final CuotaDAO cuotaDAO;
    private final ProductoDAO productoDAO;
    private final ClienteDAO clienteDAO;
    private final CategoriaDAO categoriaDAO;

    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.ventaContadoDAO = new VentaContadoDAO();
        this.ventaCreditoDAO = new VentaCreditoDAO();
        this.cuotaDAO = new CuotaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * Clase auxiliar para items del carrito de compra
     */
    public static class ItemVenta {
        private Integer idProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal iva;

        public ItemVenta(Integer idProducto, Integer cantidad, BigDecimal precioUnitario, BigDecimal iva) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.iva = iva;
        }

        public Integer getIdProducto() { return idProducto; }
        public Integer getCantidad() { return cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public BigDecimal getIva() { return iva; }
    }

    /**
     * Procesa una venta de contado
     */
    public Integer procesarVentaContado(Integer idCliente, Integer idVendedor, Integer idUsuario,
                                       List<ItemVenta> items) throws SQLException {
        
        // Validar que haya items
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);  // Iniciar transacción

            // 1. Validar stock de todos los productos
            for (ItemVenta item : items) {
                Producto producto = productoDAO.buscarPorId(item.getIdProducto());
                if (producto == null) {
                    throw new IllegalArgumentException("Producto no encontrado: " + item.getIdProducto());
                }
                if (producto.getStock() < item.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
                }
            }

            // 2. Calcular totales
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalIva = BigDecimal.ZERO;

            for (ItemVenta item : items) {
                BigDecimal subtotalItem = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
                BigDecimal ivaItem = subtotalItem.multiply(item.getIva());
                
                subtotal = subtotal.add(subtotalItem);
                totalIva = totalIva.add(ivaItem);
            }

            BigDecimal total = subtotal.add(totalIva);

            // 3. Crear la venta
            Venta venta = new Venta(TipoVenta.CONTADO, idCliente, idVendedor, idUsuario);
            venta.setSubtotal(subtotal);
            venta.setTotalIva(totalIva);
            venta.setTotal(total);
            venta = ventaDAO.insertar(venta);

            // 4. Crear detalles de venta y actualizar stock
            for (ItemVenta item : items) {
                DetalleVenta detalle = new DetalleVenta(
                    venta.getIdVenta(),
                    item.getIdProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getIva()
                );
                detalleVentaDAO.insertar(detalle);

                // Disminuir stock
                if (!productoDAO.disminuirStock(item.getIdProducto(), item.getCantidad())) {
                    throw new SQLException("Error al actualizar stock del producto");
                }
            }

            // 5. Crear registro de venta de contado
            VentaContado ventaContado = new VentaContado(venta.getIdVenta(), total);
            ventaContadoDAO.insertar(ventaContado);

            conn.commit();  // Confirmar transacción
            return venta.getIdVenta();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Revertir cambios
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Procesa una venta a crédito
     */
    public Integer procesarVentaCredito(Integer idCliente, Integer idVendedor, Integer idUsuario,
                                       List<ItemVenta> items, Integer numCuotas) throws SQLException {
        
        // Validar items
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto");
        }

        // Validar número de cuotas
        if (numCuotas != 12 && numCuotas != 18 && numCuotas != 24) {
            throw new IllegalArgumentException("El número de cuotas debe ser 12, 18 o 24");
        }

        // Validar que el cliente no tenga crédito activo
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        if (cliente.getTieneCreditoActivo()) {
            throw new IllegalArgumentException("El cliente ya tiene un crédito activo");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Validar stock
            for (ItemVenta item : items) {
                Producto producto = productoDAO.buscarPorId(item.getIdProducto());
                if (producto == null) {
                    throw new IllegalArgumentException("Producto no encontrado: " + item.getIdProducto());
                }
                if (producto.getStock() < item.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
                }
            }

            // 2. Calcular totales
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalIva = BigDecimal.ZERO;

            for (ItemVenta item : items) {
                BigDecimal subtotalItem = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
                BigDecimal ivaItem = subtotalItem.multiply(item.getIva());
                
                subtotal = subtotal.add(subtotalItem);
                totalIva = totalIva.add(ivaItem);
            }

            BigDecimal total = subtotal.add(totalIva);

            // 3. Calcular financiamiento
            // Cuota inicial: 30% del total
            BigDecimal cuotaInicial = total.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            
            // Saldo a financiar: 70% del total
            BigDecimal saldoSinInteres = total.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
            
            // Aplicar interés del 5%
            BigDecimal interes = new BigDecimal("0.05");
            BigDecimal saldoFinanciado = saldoSinInteres.multiply(interes.add(BigDecimal.ONE)).setScale(2, RoundingMode.HALF_UP);
            
            // Valor de cada cuota
            BigDecimal valorCuota = saldoFinanciado.divide(BigDecimal.valueOf(numCuotas), 2, RoundingMode.HALF_UP);

            // 4. Crear la venta
            Venta venta = new Venta(TipoVenta.CREDITO, idCliente, idVendedor, idUsuario);
            venta.setSubtotal(subtotal);
            venta.setTotalIva(totalIva);
            venta.setTotal(total);
            venta = ventaDAO.insertar(venta);

            // 5. Crear detalles y actualizar stock
            for (ItemVenta item : items) {
                DetalleVenta detalle = new DetalleVenta(
                    venta.getIdVenta(),
                    item.getIdProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario(),
                    item.getIva()
                );
                detalleVentaDAO.insertar(detalle);

                if (!productoDAO.disminuirStock(item.getIdProducto(), item.getCantidad())) {
                    throw new SQLException("Error al actualizar stock");
                }
            }

            // 6. Crear venta a crédito
            LocalDate fechaLimite = LocalDate.now().plusMonths(numCuotas);
            VentaCredito ventaCredito = new VentaCredito(
                venta.getIdVenta(),
                cuotaInicial,
                saldoFinanciado,
                numCuotas,
                valorCuota
            );
            ventaCredito.setFechaLimitePago(fechaLimite);
            ventaCredito = ventaCreditoDAO.insertar(ventaCredito);

            // 7. Generar cuotas
            LocalDate fechaVencimiento = LocalDate.now().plusMonths(1);
            for (int i = 1; i <= numCuotas; i++) {
                Cuota cuota = new Cuota(
                    ventaCredito.getIdVentaCredito(),
                    i,
                    fechaVencimiento,
                    valorCuota
                );
                cuotaDAO.insertar(cuota);
                fechaVencimiento = fechaVencimiento.plusMonths(1);
            }

            // 8. Marcar cliente con crédito activo
            clienteDAO.actualizarEstadoCredito(idCliente, true);

            conn.commit();
            return venta.getIdVenta();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Crea un item de venta con la información del producto
     */
    public ItemVenta crearItem(Integer idProducto, Integer cantidad) throws SQLException {
        Producto producto = productoDAO.buscarPorId(idProducto);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        Categoria categoria = categoriaDAO.buscarPorId(producto.getIdCategoria());
        if (categoria == null) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }

        return new ItemVenta(
            idProducto,
            cantidad,
            producto.getValorVenta(),
            categoria.getIva()
        );
    }

    /**
     * Busca una venta por ID
     */
    public Venta buscarVenta(Integer id) throws SQLException {
        return ventaDAO.buscarPorId(id);
    }

    /**
     * Lista ventas por cliente
     */
    public List<Venta> listarVentasPorCliente(Integer idCliente) throws SQLException {
        return ventaDAO.listarPorCliente(idCliente);
    }

    /**
     * Lista todas las ventas
     */
    public List<Venta> listarTodasVentas() throws SQLException {
        return ventaDAO.listarTodas();
    }

    /**
     * Lista ventas por rango de fechas
     */
    public List<Venta> listarVentasPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        return ventaDAO.listarPorFecha(fechaInicio, fechaFin);
    }
}