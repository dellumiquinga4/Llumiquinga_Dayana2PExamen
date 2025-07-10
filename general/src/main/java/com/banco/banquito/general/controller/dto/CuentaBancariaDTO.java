package com.banco.banquito.general.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Schema(description = "Cuenta Bancaria DTO")
public class CuentaBancariaDTO {

    @Schema(description = "Identificador único de la cuenta", example = "507f1f77bcf86cd799439011")
    private String id;

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

    @NotBlank(message = "El estado de cuenta es requerido")
    @Pattern(regexp = "ACTIVA|INACTIVA|BLOQUEADA|CERRADA", message = "El estado debe ser ACTIVA, INACTIVA, BLOQUEADA o CERRADA")
    @Schema(description = "Estado actual de la cuenta", example = "ACTIVA")
    private String estadoCuenta;

    @NotNull(message = "El saldo disponible es requerido")
    @DecimalMin(value = "0.00", message = "El saldo disponible no puede ser negativo")
    @Schema(description = "Saldo disponible para transacciones", example = "1500.50")
    private BigDecimal saldoDisponible;

    @NotNull(message = "El saldo contable es requerido")
    @DecimalMin(value = "0.00", message = "El saldo contable no puede ser negativo")
    @Schema(description = "Saldo contable de la cuenta", example = "1500.50")
    private BigDecimal saldoContable;

    @DecimalMin(value = "0.00", message = "El límite de sobregiro no puede ser negativo")
    @Schema(description = "Límite de sobregiro autorizado", example = "500.00")
    private BigDecimal limiteSobregiro;

    @Schema(description = "Fecha de creación de la cuenta", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaUltimaActualizacion;

    @NotBlank(message = "La moneda es requerida")
    @Pattern(regexp = "USD|EUR|COP", message = "La moneda debe ser USD, EUR o COP")
    @Schema(description = "Moneda de la cuenta", example = "USD")
    private String moneda;

    @Size(max = 10, message = "El código de sucursal no puede exceder 10 caracteres")
    @Schema(description = "Código de sucursal", example = "001")
    private String sucursal;

    @Size(max = 50, message = "El nombre del ejecutivo no puede exceder 50 caracteres")
    @Schema(description = "Ejecutivo asignado a la cuenta", example = "María González")
    private String ejecutivo;

    @NotNull(message = "El campo permite débito es requerido")
    @Schema(description = "Indica si la cuenta permite débitos", example = "true")
    private Boolean permiteDebito;

    @NotNull(message = "El campo permite crédito es requerido")
    @Schema(description = "Indica si la cuenta permite créditos", example = "true")
    private Boolean permiteCredito;

    @NotNull(message = "El campo genera extractos es requerido")
    @Schema(description = "Indica si la cuenta genera extractos", example = "true")
    private Boolean generaExtractos;

    @Schema(description = "Días de inactividad", example = "0")
    private Integer diasInactividad;
} 