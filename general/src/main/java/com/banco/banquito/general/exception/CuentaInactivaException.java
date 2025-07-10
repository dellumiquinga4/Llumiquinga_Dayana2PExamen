package com.banco.banquito.general.exception;

public class CuentaInactivaException extends RuntimeException {

    private final String numeroCuenta;
    private final String estado;

    public CuentaInactivaException(String numeroCuenta, String estado) {
        super();
        this.numeroCuenta = numeroCuenta;
        this.estado = estado;
    }

    @Override
    public String getMessage() {
        return String.format("La cuenta %s se encuentra en estado %s y no puede realizar operaciones", numeroCuenta, estado);
    }
} 