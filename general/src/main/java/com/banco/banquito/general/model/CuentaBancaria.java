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

@Document(collection = "cuentas_bancarias")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CuentaBancaria {

    @Id
    private String id;

    @Indexed(unique = true)
    private String numeroCuenta;

    @Indexed
    private String clienteIdentificacion;

    private String clienteNombre;

    private String tipoCuenta; // AHORROS, CORRIENTE, PLAZO_FIJO, VISTA

    private String estadoCuenta; // ACTIVA, INACTIVA, BLOQUEADA, CERRADA

    private BigDecimal saldoDisponible;

    private BigDecimal saldoContable;

    private BigDecimal limiteSobregiro;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaUltimaActualizacion;

    private String moneda;

    private String sucursal;

    private String ejecutivo;

    private Boolean permiteDebito;

    private Boolean permiteCredito;

    private Boolean generaExtractos;

    private Integer diasInactividad;

    // Constructor solo para primary key
    public CuentaBancaria(String id) {
        this.id = id;
    }

    // Constructor para crear nueva cuenta
    public CuentaBancaria(String numeroCuenta, String clienteIdentificacion, String clienteNombre, String tipoCuenta) {
        this.numeroCuenta = numeroCuenta;
        this.clienteIdentificacion = clienteIdentificacion;
        this.clienteNombre = clienteNombre;
        this.tipoCuenta = tipoCuenta;
        this.estadoCuenta = "ACTIVA";
        this.saldoDisponible = BigDecimal.ZERO;
        this.saldoContable = BigDecimal.ZERO;
        this.limiteSobregiro = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.moneda = "USD";
        this.permiteDebito = true;
        this.permiteCredito = true;
        this.generaExtractos = true;
        this.diasInactividad = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CuentaBancaria that = (CuentaBancaria) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Métodos de negocio
    public boolean puedeRealizarDebito(BigDecimal monto) {
        if (!permiteDebito || !"ACTIVA".equals(estadoCuenta)) {
            return false;
        }
        BigDecimal saldoDisponibleTotal = saldoDisponible.add(limiteSobregiro);
        return saldoDisponibleTotal.compareTo(monto) >= 0;
    }

    public boolean puedeRealizarCredito() {
        return permiteCredito && "ACTIVA".equals(estadoCuenta);
    }

    public void actualizarSaldos(BigDecimal monto, boolean esDebito) {
        if (esDebito) {
            this.saldoDisponible = this.saldoDisponible.subtract(monto);
            this.saldoContable = this.saldoContable.subtract(monto);
        } else {
            this.saldoDisponible = this.saldoDisponible.add(monto);
            this.saldoContable = this.saldoContable.add(monto);
        }
        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.diasInactividad = 0;
    }
} 