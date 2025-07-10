package com.banco.banquito.general.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "movimientos_cuenta")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MovimientoCuenta {

    @Id
    private String id;

    @Indexed
    private String numeroCuenta;

    @Indexed
    private String numeroComprobante;

    private String tipoMovimiento; // DEBITO, CREDITO

    private BigDecimal monto;

    private BigDecimal saldoAnterior;

    private BigDecimal saldoPosterior;

    private String concepto;

    private String descripcion;

    private LocalDateTime fechaMovimiento;

    private LocalDateTime fechaValor;

    private String sucursal;

    private String cajero;

    private String canalTransaccion;

    private String referenciaExterna;

    private String observaciones;

    private Boolean procesado;

    private Boolean reversado;

    private String movimientoReverso;

    // Constructor solo para primary key
    public MovimientoCuenta(String id) {
        this.id = id;
    }

    // Constructor para crear nuevo movimiento
    public MovimientoCuenta(String numeroCuenta, String numeroComprobante, String tipoMovimiento, 
                           BigDecimal monto, BigDecimal saldoAnterior, String concepto) {
        this.numeroCuenta = numeroCuenta;
        this.numeroComprobante = numeroComprobante;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.saldoAnterior = saldoAnterior;
        this.concepto = concepto;
        this.fechaMovimiento = LocalDateTime.now();
        this.fechaValor = LocalDateTime.now();
        this.procesado = false;
        this.reversado = false;
        
        // Calcular saldo posterior
        if ("DEBITO".equals(tipoMovimiento)) {
            this.saldoPosterior = saldoAnterior.subtract(monto);
        } else {
            this.saldoPosterior = saldoAnterior.add(monto);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MovimientoCuenta that = (MovimientoCuenta) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Métodos de negocio
    public void procesar() {
        this.procesado = true;
        this.fechaValor = LocalDateTime.now();
    }

    public void reversar(String movimientoReversoId) {
        this.reversado = true;
        this.movimientoReverso = movimientoReversoId;
        this.observaciones = "Movimiento reversado";
    }
} 