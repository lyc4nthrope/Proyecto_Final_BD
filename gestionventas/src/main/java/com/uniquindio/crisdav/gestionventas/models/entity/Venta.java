package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.TipoVenta;
import com.uniquindio.crisdav.gestionventas.models.enums.EstadoCredito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;

public class Venta implements Serializable {
    private Long id;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long createdByUserId;
    private Long updatedByUserId;

    private String numeroFactura;
    private LocalDate fecha;
    private Long clienteId;
    private Cliente cliente;           // referencia opcional
    private TipoVenta tipoVenta;
    private BigDecimal totalSinIva;
    private BigDecimal totalIva;
    private BigDecimal total;
    private BigDecimal inicialPago;    // 30% si credito
    private Integer plazoMeses;        // 12/18/24 si credito
    private BigDecimal tasaInteres;    // 0.05 -> 5%
    private EstadoCredito estadoCredito;

    // Colecciones
    private List<DetalleVenta> detalles = new ArrayList<>();
    private List<Cuota> cuotas = new ArrayList<>();

    public Venta() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.fecha = LocalDate.now();
        this.tipoVenta = TipoVenta.CONTADO;
        this.tasaInteres = BigDecimal.ZERO;
        this.estadoCredito = EstadoCredito.N_A;
    }

    // ejemplo de método para agregar detalle
    public void addDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            detalle.setVentaId(this.id);
            recalcularTotales();
        }
    }

    // recalcula totales sumando detalles
    public void recalcularTotales() {
        BigDecimal sumSubtotal = BigDecimal.ZERO;
        BigDecimal sumIva = BigDecimal.ZERO;
        for (DetalleVenta d : detalles) {
            BigDecimal sub = d.calcularSubtotal();
            sumSubtotal = sumSubtotal.add(sub == null ? BigDecimal.ZERO : sub);
            if (d.getIvaItem() != null) sumIva = sumIva.add(d.getIvaItem());
        }
        this.totalSinIva = sumSubtotal.setScale(2, RoundingMode.HALF_UP);
        this.totalIva = sumIva.setScale(2, RoundingMode.HALF_UP);
        this.total = this.totalSinIva.add(this.totalIva).setScale(2, RoundingMode.HALF_UP);
    }

    // helper para generar cuotas cuando es credito
    public void generarCuotasParaCredito() {
        if (this.tipoVenta != TipoVenta.CREDITO || this.plazoMeses == null) return;

        // inicial 30%
        this.inicialPago = this.total.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoBase = this.total.subtract(this.inicialPago);
        // aplicar 5% sobre el saldo (ajuste según regla de negocio)
        BigDecimal saldoConInteres = saldoBase.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cuotaValor = saldoConInteres.divide(new BigDecimal(this.plazoMeses), 2, RoundingMode.HALF_UP);

        cuotas.clear();
        for (int i = 1; i <= this.plazoMeses; i++) {
            Cuota c = new Cuota();
            c.setVentaId(this.id);
            c.setNumeroCuota(i);
            c.setValor(cuotaValor);
            c.setFechaVencimiento(this.fecha.plusMonths(i)); // vencimiento mensual a partir de la fecha de la venta
            cuotas.add(c);
        }
        this.tasaInteres = new BigDecimal("0.05");
        this.estadoCredito = EstadoCredito.ABIERTO;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public Long getUpdatedByUserId() { return updatedByUserId; }
    public void setUpdatedByUserId(Long updatedByUserId) { this.updatedByUserId = updatedByUserId; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoVenta getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(TipoVenta tipoVenta) { this.tipoVenta = tipoVenta; }

    public BigDecimal getTotalSinIva() { return totalSinIva; }
    public void setTotalSinIva(BigDecimal totalSinIva) { this.totalSinIva = totalSinIva; }

    public BigDecimal getTotalIva() { return totalIva; }
    public void setTotalIva(BigDecimal totalIva) { this.totalIva = totalIva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getInicialPago() { return inicialPago; }
    public void setInicialPago(BigDecimal inicialPago) { this.inicialPago = inicialPago; }

    public Integer getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(Integer plazoMeses) { this.plazoMeses = plazoMeses; }

    public BigDecimal getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(BigDecimal tasaInteres) { this.tasaInteres = tasaInteres; }

    public EstadoCredito getEstadoCredito() { return estadoCredito; }
    public void setEstadoCredito(EstadoCredito estadoCredito) { this.estadoCredito = estadoCredito; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public List<Cuota> getCuotas() { return cuotas; }
    public void setCuotas(List<Cuota> cuotas) { this.cuotas = cuotas; }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", numeroFactura='" + numeroFactura + '\'' +
                ", fecha=" + fecha +
                ", tipoVenta=" + tipoVenta +
                ", total=" + total +
                '}';
    }
}
