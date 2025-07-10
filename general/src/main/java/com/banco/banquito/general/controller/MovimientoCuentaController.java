package com.banco.banquito.general.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banquito.general.controller.dto.MovimientoCuentaDTO;
import com.banco.banquito.general.controller.mapper.MovimientoCuentaMapper;
import com.banco.banquito.general.exception.CuentaInactivaException;
import com.banco.banquito.general.exception.CuentaNotFoundException;
import com.banco.banquito.general.exception.SaldoInsuficienteException;
import com.banco.banquito.general.model.MovimientoCuenta;
import com.banco.banquito.general.service.MovimientoCuentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/movimientos")
@Tag(name = "Movimientos de Cuenta", description = "Operaciones de gestión de movimientos de cuentas bancarias")
public class MovimientoCuentaController {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoCuentaController.class);

    private final MovimientoCuentaService service;
    private final MovimientoCuentaMapper mapper;

    public MovimientoCuentaController(MovimientoCuentaService service, MovimientoCuentaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los movimientos", description = "Retorna una lista paginada de todos los movimientos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida exitosamente")
    })
    public ResponseEntity<List<MovimientoCuentaDTO>> getAllMovimientos(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaMovimiento") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        logger.info("Consultando todos los movimientos - página: {}, tamaño: {}", page, size);
        
        if (page < 0 || size <= 0) {
            List<MovimientoCuenta> movimientos = service.findAll();
            List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientos.size());
            
            for (MovimientoCuenta movimiento : movimientos) {
                dtos.add(mapper.toDTO(movimiento));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<MovimientoCuenta> movimientosPage = service.findAll(page, size, sortBy, sortDirection);
        List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientosPage.getContent().size());
        
        for (MovimientoCuenta movimiento : movimientosPage.getContent()) {
            dtos.add(mapper.toDTO(movimiento));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID", description = "Retorna un movimiento por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimiento encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<MovimientoCuentaDTO> getMovimientoById(
            @Parameter(description = "ID del movimiento") @PathVariable String id) {
        
        try {
            MovimientoCuenta movimiento = service.findById(id);
            return ResponseEntity.ok(mapper.toDTO(movimiento));
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/comprobante/{numeroComprobante}")
    @Operation(summary = "Obtener movimiento por comprobante", description = "Retorna un movimiento por su número de comprobante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimiento encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<MovimientoCuentaDTO> getMovimientoByComprobante(
            @Parameter(description = "Número de comprobante") @PathVariable String numeroComprobante) {
        
        try {
            MovimientoCuenta movimiento = service.findByNumeroComprobante(numeroComprobante);
            return ResponseEntity.ok(mapper.toDTO(movimiento));
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    @Operation(summary = "Obtener movimientos por cuenta", description = "Retorna todos los movimientos de una cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados exitosamente")
    })
    public ResponseEntity<List<MovimientoCuentaDTO>> getMovimientosByCuenta(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaMovimiento") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        logger.info("Consultando movimientos de cuenta: {}", numeroCuenta);
        
        if (page < 0 || size <= 0) {
            List<MovimientoCuenta> movimientos = service.findByNumeroCuenta(numeroCuenta);
            List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientos.size());
            
            for (MovimientoCuenta movimiento : movimientos) {
                dtos.add(mapper.toDTO(movimiento));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<MovimientoCuenta> movimientosPage = service.findByNumeroCuenta(numeroCuenta, page, size, sortBy, sortDirection);
        List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientosPage.getContent().size());
        
        for (MovimientoCuenta movimiento : movimientosPage.getContent()) {
            dtos.add(mapper.toDTO(movimiento));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/cuenta/{numeroCuenta}/tipo/{tipoMovimiento}")
    @Operation(summary = "Obtener movimientos por cuenta y tipo", description = "Retorna movimientos filtrados por cuenta y tipo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados exitosamente")
    })
    public ResponseEntity<List<MovimientoCuentaDTO>> getMovimientosByCuentaYTipo(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta,
            @Parameter(description = "Tipo de movimiento (DEBITO, CREDITO)") @PathVariable String tipoMovimiento,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaMovimiento") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        logger.info("Consultando movimientos de cuenta: {} tipo: {}", numeroCuenta, tipoMovimiento);
        
        if (page < 0 || size <= 0) {
            List<MovimientoCuenta> movimientos = service.findByTipoMovimiento(numeroCuenta, tipoMovimiento);
            List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientos.size());
            
            for (MovimientoCuenta movimiento : movimientos) {
                dtos.add(mapper.toDTO(movimiento));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<MovimientoCuenta> movimientosPage = service.findByTipoMovimiento(numeroCuenta, tipoMovimiento, page, size, sortBy, sortDirection);
        List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientosPage.getContent().size());
        
        for (MovimientoCuenta movimiento : movimientosPage.getContent()) {
            dtos.add(mapper.toDTO(movimiento));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/cuenta/{numeroCuenta}/fechas")
    @Operation(summary = "Obtener movimientos por rango de fechas", description = "Retorna movimientos filtrados por cuenta y rango de fechas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados exitosamente")
    })
    public ResponseEntity<List<MovimientoCuentaDTO>> getMovimientosByFechas(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta,
            @Parameter(description = "Fecha inicial") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @Parameter(description = "Tipo de movimiento (DEBITO, CREDITO)") @RequestParam(required = false) String tipoMovimiento,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaMovimiento") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        logger.info("Consultando movimientos de cuenta: {} entre {} y {}", numeroCuenta, fechaInicio, fechaFin);
        
        if (page < 0 || size <= 0) {
            List<MovimientoCuenta> movimientos;
            if (tipoMovimiento != null) {
                movimientos = service.findByRangoFechas(numeroCuenta, fechaInicio, fechaFin);
            } else {
                movimientos = service.findByRangoFechas(numeroCuenta, fechaInicio, fechaFin);
            }
            
            List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientos.size());
            for (MovimientoCuenta movimiento : movimientos) {
                dtos.add(mapper.toDTO(movimiento));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<MovimientoCuenta> movimientosPage = service.findByRangoFechas(numeroCuenta, fechaInicio, fechaFin, page, size, sortBy, sortDirection);
        List<MovimientoCuentaDTO> dtos = new ArrayList<>(movimientosPage.getContent().size());
        
        for (MovimientoCuenta movimiento : movimientosPage.getContent()) {
            dtos.add(mapper.toDTO(movimiento));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(summary = "Procesar nuevo movimiento", description = "Procesa un nuevo movimiento de cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movimiento procesado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto - movimiento ya existe")
    })
    public ResponseEntity<MovimientoCuentaDTO> procesarMovimiento(
            @Parameter(description = "Datos del movimiento a procesar") @Valid @RequestBody MovimientoCuentaDTO movimientoDTO) {
        
        try {
            MovimientoCuenta movimiento = service.procesarMovimiento(movimientoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(movimiento));
        } catch (RuntimeException e) {
            logger.error("Error al procesar movimiento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reversar/{numeroComprobante}")
    @Operation(summary = "Reversar movimiento", description = "Reversa un movimiento existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimiento reversado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "400", description = "El movimiento no puede ser reversado")
    })
    public ResponseEntity<MovimientoCuentaDTO> reversarMovimiento(
            @Parameter(description = "Número de comprobante del movimiento a reversar") @PathVariable String numeroComprobante,
            @Parameter(description = "Motivo del reverso") @RequestParam String motivo) {
        
        try {
            MovimientoCuenta movimientoReverso = service.reversarMovimiento(numeroComprobante, motivo);
            return ResponseEntity.ok(mapper.toDTO(movimientoReverso));
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            logger.error("Error al reversar movimiento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/generar-comprobante")
    @Operation(summary = "Generar número de comprobante", description = "Genera un nuevo número de comprobante único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Número de comprobante generado exitosamente")
    })
    public ResponseEntity<String> generarNumeroComprobante() {
        String numeroComprobante = service.generarNumeroComprobante();
        return ResponseEntity.ok(numeroComprobante);
    }

    @GetMapping("/cuenta/{numeroCuenta}/estadisticas")
    @Operation(summary = "Obtener estadísticas de movimientos", description = "Retorna estadísticas de movimientos de una cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    public ResponseEntity<EstadisticasMovimientosDTO> getEstadisticasMovimientos(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta) {
        
        logger.info("Consultando estadísticas de movimientos para cuenta: {}", numeroCuenta);
        
        try {
            long totalMovimientos = service.contarMovimientos(numeroCuenta);
            long totalDebitos = service.contarMovimientosPorTipo(numeroCuenta, "DEBITO");
            long totalCreditos = service.contarMovimientosPorTipo(numeroCuenta, "CREDITO");
            
            EstadisticasMovimientosDTO estadisticas = new EstadisticasMovimientosDTO(
                numeroCuenta, totalMovimientos, totalDebitos, totalCreditos);
            
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cuenta/{numeroCuenta}/ultimo")
    @Operation(summary = "Obtener último movimiento", description = "Retorna el último movimiento de una cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Último movimiento obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron movimientos")
    })
    public ResponseEntity<MovimientoCuentaDTO> getUltimoMovimiento(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta) {
        
        try {
            MovimientoCuenta ultimoMovimiento = service.obtenerUltimoMovimiento(numeroCuenta);
            return ResponseEntity.ok(mapper.toDTO(ultimoMovimiento));
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler({CuentaNotFoundException.class})
    public ResponseEntity<String> handleCuentaNotFound(CuentaNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({CuentaInactivaException.class})
    public ResponseEntity<String> handleCuentaInactiva(CuentaInactivaException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({SaldoInsuficienteException.class})
    public ResponseEntity<String> handleSaldoInsuficiente(SaldoInsuficienteException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Clase interna para las estadísticas
    public static class EstadisticasMovimientosDTO {
        public final String numeroCuenta;
        public final long totalMovimientos;
        public final long totalDebitos;
        public final long totalCreditos;

        public EstadisticasMovimientosDTO(String numeroCuenta, long totalMovimientos, long totalDebitos, long totalCreditos) {
            this.numeroCuenta = numeroCuenta;
            this.totalMovimientos = totalMovimientos;
            this.totalDebitos = totalDebitos;
            this.totalCreditos = totalCreditos;
        }
    }
} 