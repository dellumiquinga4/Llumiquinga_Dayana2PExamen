package com.banco.banquito.general.controller.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO para crear una nueva cuenta bancaria")
public class CrearCuentaDTO {

    @NotBlank(message = "El número de cuenta es requerido")
    @Size(min = 10, max = 20, message = "El número de cuenta debe tener entre 10 y 20 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta solo puede contener números")
    @Schema(description = "Número único de la cuenta", example = "1234567890")
    private String numeroCuenta;

    @NotBlank(message = "La identificación del cliente es requerida")
    @Size(min = 8, max = 13, message = "La identificación debe tener entre 8 y 13 caracteres")
    @Schema(description = "Identificación del cliente titular", example = "1234567890")
    private String clienteIdentificacion;

    @NotBlank(message = "El nombre del cliente es requerido")
    @Size(min = 5, max = 100, message = "El nombre del cliente debe tener entre 5 y 100 caracteres")
    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez García")
    private String clienteNombre;

    @NotBlank(message = "El tipo de cuenta es requerido")
    @Pattern(regexp = "AHORROS|CORRIENTE|PLAZO_FIJO|VISTA", message = "El tipo de cuenta debe ser AHORROS, CORRIENTE, PLAZO_FIJO o VISTA")
    @Schema(description = "Tipo de cuenta bancaria", example = "AHORROS")
    private String tipoCuenta;

    @DecimalMin(value = "0.00", message = "El saldo inicial no puede ser negativo")
    @Schema(description = "Saldo inicial de la cuenta", example = "100.00")
    private BigDecimal saldoInicial;

    @DecimalMin(value = "0.00", message = "El límite de sobregiro no puede ser negativo")
    @Schema(description = "Límite de sobregiro autorizado", example = "500.00")
    private BigDecimal limiteSobregiro;

    @Pattern(regexp = "USD|EUR|COP", message = "La moneda debe ser USD, EUR o COP")
    @Schema(description = "Moneda de la cuenta", example = "USD", defaultValue = "USD")
    private String moneda;

    @Size(max = 10, message = "El código de sucursal no puede exceder 10 caracteres")
    @Schema(description = "Código de sucursal", example = "001")
    private String sucursal;

    @Size(max = 50, message = "El nombre del ejecutivo no puede exceder 50 caracteres")
    @Schema(description = "Ejecutivo asignado a la cuenta", example = "María González")
    private String ejecutivo;

    @NotNull(message = "El ID de la solicitud es requerido para idempotencia")
    @Schema(description = "ID único de la solicitud para evitar duplicados", example = "REQ-2024-001")
    private String requestId;
} 