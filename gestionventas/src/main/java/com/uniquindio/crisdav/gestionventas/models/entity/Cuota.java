package com.uniquindio.crisdav.gestionventas.models.entity;

import com.uniquindio.crisdav.gestionventas.models.enums.EstadoCuota;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Cuota implements Serializable {
    private Long id;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long createdByUserId;
    private Long updatedByUserId;

    private Long ventaId;
    private Integer numeroCuota;    // 0 = inicial (si la manejas así), 1..N mensual
    private LocalDate fechaVencimiento;
    private BigDecimal valor;
    private BigDecimal pagado;
    private LocalDate fechaPago;
    private EstadoCuota estado;

    public Cuota() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.pagado = BigDecimal.ZERO;
        this.estado = EstadoCuota.PENDIENTE;
    }

    public boolean estaPagada() {
        return pagado != null && pagado.compareTo(valor) >= 0;
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

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public Integer getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(Integer numeroCuota) { this.numeroCuota = numeroCuota; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public BigDecimal getPagado() { return pagado; }
    public void setPagado(BigDecimal pagado) { this.pagado = pagado; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public EstadoCuota getEstado() { return estado; }
    public void setEstado(EstadoCuota estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Cuota{" +
                "id=" + id +
                ", ventaId=" + ventaId +
                ", numeroCuota=" + numeroCuota +
                ", valor=" + valor +
                ", estado=" + estado +
                '}';
    }
}

