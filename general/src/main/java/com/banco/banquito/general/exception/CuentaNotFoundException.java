package com.banco.banquito.general.exception;

public class CuentaNotFoundException extends RuntimeException {

    private final String numeroCuenta;
    private final String cliente;

    public CuentaNotFoundException(String numeroCuenta) {
        super();
        this.numeroCuenta = numeroCuenta;
        this.cliente = null;
    }

    public CuentaNotFoundException(String numeroCuenta, String cliente) {
        super();
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
    }

    @Override
    public String getMessage() {
        if (cliente != null) {
            return String.format("No se encontró ninguna cuenta con número: %s para el cliente: %s", numeroCuenta, cliente);
        }
        return String.format("No se encontró ninguna cuenta con número: %s", numeroCuenta);
    }
} 