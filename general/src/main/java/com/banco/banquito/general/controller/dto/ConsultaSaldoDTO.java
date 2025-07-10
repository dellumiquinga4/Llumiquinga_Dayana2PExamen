package com.banco.banquito.general.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO para consulta de saldos de cuenta")
public class ConsultaSaldoDTO {

    @Schema(description = "Número único de la cuenta", example = "1234567890")
    private String numeroCuenta;

    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez García")
    private String clienteNombre;

    @Schema(description = "Tipo de cuenta bancaria", example = "AHORROS")
    private String tipoCuenta;

    @Schema(description = "Estado actual de la cuenta", example = "ACTIVA")
    private String estadoCuenta;

    @Schema(description = "Saldo disponible para transacciones", example = "1500.50")
    private BigDecimal saldoDisponible;

    @Schema(description = "Saldo contable de la cuenta", example = "1500.50")
    private BigDecimal saldoContable;

    @Schema(description = "Límite de sobregiro autorizado", example = "500.00")
    private BigDecimal limiteSobregiro;

    @Schema(description = "Saldo disponible total (incluye sobregiro)", example = "2000.50")
    private BigDecimal saldoDisponibleTotal;

    @Schema(description = "Moneda de la cuenta", example = "USD")
    private String moneda;

    @Schema(description = "Fecha de última actualización", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaUltimaActualizacion;

    @Schema(description = "Días de inactividad", example = "0")
    private Integer diasInactividad;

    @Schema(description = "Indica si la cuenta puede realizar débitos", example = "true")
    private Boolean permiteDebito;

    @Schema(description = "Indica si la cuenta puede realizar créditos", example = "true")
    private Boolean permiteCredito;
} 