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
@Schema(description = "DTO para movimientos de cuenta")
public class MovimientoCuentaDTO {

    @Schema(description = "Identificador único del movimiento", example = "507f1f77bcf86cd799439011")
    private String id;

    @NotBlank(message = "El número de cuenta es requerido")
    @Schema(description = "Número de cuenta asociada", example = "1234567890")
    private String numeroCuenta;

    @NotBlank(message = "El número de comprobante es requerido")
    @Size(min = 8, max = 20, message = "El número de comprobante debe tener entre 8 y 20 caracteres")
    @Schema(description = "Número único del comprobante", example = "COMP-2024-001")
    private String numeroComprobante;

    @NotBlank(message = "El tipo de movimiento es requerido")
    @Pattern(regexp = "DEBITO|CREDITO", message = "El tipo de movimiento debe ser DEBITO o CREDITO")
    @Schema(description = "Tipo de movimiento", example = "DEBITO")
    private String tipoMovimiento;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Schema(description = "Monto del movimiento", example = "150.75")
    private BigDecimal monto;

    @Schema(description = "Saldo anterior al movimiento", example = "1500.50")
    private BigDecimal saldoAnterior;

    @Schema(description = "Saldo posterior al movimiento", example = "1349.75")
    private BigDecimal saldoPosterior;

    @NotBlank(message = "El concepto es requerido")
    @Size(min = 3, max = 50, message = "El concepto debe tener entre 3 y 50 caracteres")
    @Schema(description = "Concepto del movimiento", example = "RETIRO CAJERO")
    private String concepto;

    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Schema(description = "Descripción detallada del movimiento", example = "Retiro en cajero automático ubicado en...")
    private String descripcion;

    @Schema(description = "Fecha del movimiento", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaMovimiento;

    @Schema(description = "Fecha valor del movimiento", example = "2024-01-15T10:30:00")
    private LocalDateTime fechaValor;

    @Size(max = 10, message = "El código de sucursal no puede exceder 10 caracteres")
    @Schema(description = "Código de sucursal", example = "001")
    private String sucursal;

    @Size(max = 50, message = "El nombre del cajero no puede exceder 50 caracteres")
    @Schema(description = "Cajero que procesó la transacción", example = "Sistema ATM")
    private String cajero;

    @Size(max = 20, message = "El canal de transacción no puede exceder 20 caracteres")
    @Schema(description = "Canal utilizado para la transacción", example = "ATM")
    private String canalTransaccion;

    @Size(max = 50, message = "La referencia externa no puede exceder 50 caracteres")
    @Schema(description = "Referencia externa del movimiento", example = "REF-EXT-001")
    private String referenciaExterna;

    @Size(max = 200, message = "Las observaciones no pueden exceder 200 caracteres")
    @Schema(description = "Observaciones adicionales", example = "Transacción procesada correctamente")
    private String observaciones;

    @Schema(description = "Indica si el movimiento está procesado", example = "true")
    private Boolean procesado;

    @Schema(description = "Indica si el movimiento fue reversado", example = "false")
    private Boolean reversado;

    @Schema(description = "ID del movimiento de reverso", example = "507f1f77bcf86cd799439012")
    private String movimientoReverso;
} 