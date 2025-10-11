package com.uniquindio.crisdav.gestionventas.controllers;

import com.uniquindio.crisdav.gestionventas.dao.ClienteDAO;
import com.uniquindio.crisdav.gestionventas.dao.CuotaDAO;
import com.uniquindio.crisdav.gestionventas.dao.VentaCreditoDAO;
import com.uniquindio.crisdav.gestionventas.dao.VentaDAO;
import com.uniquindio.crisdav.gestionventas.models.entity.Cuota;
import com.uniquindio.crisdav.gestionventas.models.entity.Venta;
import com.uniquindio.crisdav.gestionventas.models.entity.VentaCredito;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoCuota;
import com.uniquindio.crisdav.gestionventas.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CuotaController {
    
    private final CuotaDAO cuotaDAO;
    private final VentaCreditoDAO ventaCreditoDAO;
    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;

    public CuotaController() {
        this.cuotaDAO = new CuotaDAO();
        this.ventaCreditoDAO = new VentaCreditoDAO();
        this.ventaDAO = new VentaDAO();
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Registra el pago de una cuota
     */
    public boolean pagarCuota(Integer idCuota) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Buscar la cuota
            Cuota cuota = cuotaDAO.buscarPorId(idCuota);
            if (cuota == null) {
                throw new IllegalArgumentException("Cuota no encontrada");
            }

            // Verificar que no esté pagada
            if (cuota.getEstado() == EstadoCuota.PAGADA) {
                throw new IllegalArgumentException("La cuota ya está pagada");
            }

            // 2. Marcar como pagada
            cuotaDAO.actualizarEstado(idCuota, EstadoCuota.PAGADA, LocalDate.now());

            // 3. Actualizar saldo pendiente de la venta a crédito
            VentaCredito ventaCredito = ventaCreditoDAO.buscarPorId(cuota.getIdVentaCredito());
            BigDecimal nuevoSaldo = ventaCredito.getSaldoPendiente().subtract(cuota.getValorCuota());
            
            // Asegurar que no quede saldo negativo por redondeos
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                nuevoSaldo = BigDecimal.ZERO;
            }

            ventaCreditoDAO.actualizarSaldoPendiente(ventaCredito.getIdVentaCredito(), nuevoSaldo);

            // 4. Si se pagó todo, liberar el crédito del cliente
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) == 0) {
                Venta venta = ventaDAO.buscarPorId(ventaCredito.getIdVenta());
                clienteDAO.actualizarEstadoCredito(venta.getIdCliente(), false);
            }

            conn.commit();
            return true;

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
     * Lista las cuotas de una venta a crédito
     */
    public List<Cuota> listarCuotasPorVentaCredito(Integer idVentaCredito) throws SQLException {
        return cuotaDAO.listarPorVentaCredito(idVentaCredito);
    }

    /**
     * Lista las cuotas pendientes de una venta a crédito
     */
    public List<Cuota> listarCuotasPendientes(Integer idVentaCredito) throws SQLException {
        return cuotaDAO.listarPendientesPorVentaCredito(idVentaCredito);
    }

    /**
     * Lista todas las cuotas vencidas del sistema
     */
    public List<Cuota> listarCuotasVencidas() throws SQLException {
        return cuotaDAO.listarVencidas();
    }

    /**
     * Actualiza el estado de cuotas pendientes que ya vencieron
     */
    public boolean actualizarCuotasVencidas() throws SQLException {
        return cuotaDAO.marcarVencidas();
    }

    /**
     * Busca una cuota por ID
     */
    public Cuota buscarCuota(Integer id) throws SQLException {
        return cuotaDAO.buscarPorId(id);
    }

    /**
     * Obtiene información de la venta a crédito
     */
    public VentaCredito obtenerVentaCredito(Integer idVentaCredito) throws SQLException {
        return ventaCreditoDAO.buscarPorId(idVentaCredito);
    }
}