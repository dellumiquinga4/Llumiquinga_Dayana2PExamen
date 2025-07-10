package com.banco.banquito.general.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {

    private final String numeroCuenta;
    private final BigDecimal saldoDisponible;
    private final BigDecimal montoSolicitado;

    public SaldoInsuficienteException(String numeroCuenta, BigDecimal saldoDisponible, BigDecimal montoSolicitado) {
        super();
        this.numeroCuenta = numeroCuenta;
        this.saldoDisponible = saldoDisponible;
        this.montoSolicitado = montoSolicitado;
    }

    @Override
    public String getMessage() {
        return String.format("Saldo insuficiente en cuenta %s. Saldo disponible: $%.2f, Monto solicitado: $%.2f", 
                numeroCuenta, saldoDisponible, montoSolicitado);
    }
} 