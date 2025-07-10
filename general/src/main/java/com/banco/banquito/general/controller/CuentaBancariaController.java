package com.banco.banquito.general.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.banquito.general.controller.dto.ConsultaSaldoDTO;
import com.banco.banquito.general.controller.dto.CrearCuentaDTO;
import com.banco.banquito.general.controller.dto.CuentaBancariaDTO;
import com.banco.banquito.general.controller.mapper.CuentaBancariaMapper;
import com.banco.banquito.general.exception.CuentaInactivaException;
import com.banco.banquito.general.exception.CuentaNotFoundException;
import com.banco.banquito.general.exception.SaldoInsuficienteException;
import com.banco.banquito.general.model.CuentaBancaria;
import com.banco.banquito.general.service.CuentaBancariaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/cuentas")
@Tag(name = "Cuentas Bancarias", description = "Operaciones de gestión de cuentas bancarias")
public class CuentaBancariaController {

    private final CuentaBancariaService service;
    private final CuentaBancariaMapper mapper;

    public CuentaBancariaController(CuentaBancariaService service, CuentaBancariaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las cuentas bancarias", description = "Retorna una lista paginada de todas las cuentas bancarias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida exitosamente")
    })
    public ResponseEntity<List<CuentaBancariaDTO>> getAllCuentas(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        if (page < 0 || size <= 0) {
            List<CuentaBancaria> cuentas = service.findAll();
            List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentas.size());
            
            for (CuentaBancaria cuenta : cuentas) {
                dtos.add(mapper.toDTO(cuenta));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<CuentaBancaria> cuentasPage = service.findAll(page, size, sortBy, sortDirection);
        List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentasPage.getContent().size());
        
        for (CuentaBancaria cuenta : cuentasPage.getContent()) {
            dtos.add(mapper.toDTO(cuenta));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID", description = "Retorna una cuenta bancaria por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaBancariaDTO> getCuentaById(
            @Parameter(description = "ID de la cuenta") @PathVariable String id) {
        
        CuentaBancaria cuenta = service.findById(id);
        return ResponseEntity.ok(mapper.toDTO(cuenta));
    }

    @GetMapping("/numero/{numeroCuenta}")
    @Operation(summary = "Obtener cuenta por número", description = "Retorna una cuenta bancaria por su número")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaBancariaDTO> getCuentaByNumero(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta) {
        
        CuentaBancaria cuenta = service.findByNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(mapper.toDTO(cuenta));
    }

    @GetMapping("/cliente/{clienteIdentificacion}")
    @Operation(summary = "Obtener cuentas por cliente", description = "Retorna todas las cuentas de un cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuentas encontradas exitosamente")
    })
    public ResponseEntity<List<CuentaBancariaDTO>> getCuentasByCliente(
            @Parameter(description = "Identificación del cliente") @PathVariable String clienteIdentificacion,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        if (page < 0 || size <= 0) {
            List<CuentaBancaria> cuentas = service.findByClienteIdentificacion(clienteIdentificacion);
            List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentas.size());
            
            for (CuentaBancaria cuenta : cuentas) {
                dtos.add(mapper.toDTO(cuenta));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<CuentaBancaria> cuentasPage = service.findByClienteIdentificacion(clienteIdentificacion, page, size, sortBy, sortDirection);
        List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentasPage.getContent().size());
        
        for (CuentaBancaria cuenta : cuentasPage.getContent()) {
            dtos.add(mapper.toDTO(cuenta));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/estado/{estadoCuenta}")
    @Operation(summary = "Obtener cuentas por estado", description = "Retorna todas las cuentas con un estado específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuentas encontradas exitosamente")
    })
    public ResponseEntity<List<CuentaBancariaDTO>> getCuentasByEstado(
            @Parameter(description = "Estado de la cuenta (ACTIVA, INACTIVA, BLOQUEADA, CERRADA)") @PathVariable String estadoCuenta,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "desc") String sortDirection) {
        
        if (page < 0 || size <= 0) {
            List<CuentaBancaria> cuentas = service.findByEstadoCuenta(estadoCuenta);
            List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentas.size());
            
            for (CuentaBancaria cuenta : cuentas) {
                dtos.add(mapper.toDTO(cuenta));
            }
            return ResponseEntity.ok(dtos);
        }
        
        Page<CuentaBancaria> cuentasPage = service.findByEstadoCuenta(estadoCuenta, page, size, sortBy, sortDirection);
        List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentasPage.getContent().size());
        
        for (CuentaBancaria cuenta : cuentasPage.getContent()) {
            dtos.add(mapper.toDTO(cuenta));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipo/{tipoCuenta}")
    @Operation(summary = "Obtener cuentas por tipo", description = "Retorna todas las cuentas de un tipo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuentas encontradas exitosamente")
    })
    public ResponseEntity<List<CuentaBancariaDTO>> getCuentasByTipo(
            @Parameter(description = "Tipo de cuenta (AHORROS, CORRIENTE, PLAZO_FIJO, VISTA)") @PathVariable String tipoCuenta) {
        
        List<CuentaBancaria> cuentas = service.findByTipoCuenta(tipoCuenta);
        List<CuentaBancariaDTO> dtos = new ArrayList<>(cuentas.size());
        
        for (CuentaBancaria cuenta : cuentas) {
            dtos.add(mapper.toDTO(cuenta));
        }
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/numero/{numeroCuenta}/saldo")
    @Operation(summary = "Consultar saldo de cuenta", description = "Retorna el saldo y información básica de una cuenta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo consultado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<ConsultaSaldoDTO> consultarSaldo(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta) {
        
        CuentaBancaria cuenta = service.findByNumeroCuenta(numeroCuenta);
        
        ConsultaSaldoDTO consultaSaldo = new ConsultaSaldoDTO();
        consultaSaldo.setNumeroCuenta(cuenta.getNumeroCuenta());
        consultaSaldo.setClienteNombre(cuenta.getClienteNombre());
        consultaSaldo.setTipoCuenta(cuenta.getTipoCuenta());
        consultaSaldo.setEstadoCuenta(cuenta.getEstadoCuenta());
        consultaSaldo.setSaldoDisponible(cuenta.getSaldoDisponible());
        consultaSaldo.setSaldoContable(cuenta.getSaldoContable());
        consultaSaldo.setLimiteSobregiro(cuenta.getLimiteSobregiro());
        consultaSaldo.setSaldoDisponibleTotal(cuenta.getSaldoDisponible().add(cuenta.getLimiteSobregiro()));
        consultaSaldo.setMoneda(cuenta.getMoneda());
        consultaSaldo.setFechaUltimaActualizacion(cuenta.getFechaUltimaActualizacion());
        consultaSaldo.setDiasInactividad(cuenta.getDiasInactividad());
        consultaSaldo.setPermiteDebito(cuenta.getPermiteDebito());
        consultaSaldo.setPermiteCredito(cuenta.getPermiteCredito());
        
        return ResponseEntity.ok(consultaSaldo);
    }

    @PostMapping
    @Operation(summary = "Crear nueva cuenta", description = "Crea una nueva cuenta bancaria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto - cuenta ya existe")
    })
    public ResponseEntity<CuentaBancariaDTO> crearCuenta(
            @Parameter(description = "Datos de la cuenta a crear") @Valid @RequestBody CrearCuentaDTO crearCuentaDTO) {
        
        CuentaBancaria cuenta = service.crearCuenta(crearCuentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(cuenta));
    }

    @PatchMapping("/numero/{numeroCuenta}/bloquear")
    @Operation(summary = "Bloquear cuenta", description = "Bloquea una cuenta bancaria activa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta bloqueada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "400", description = "La cuenta no puede ser bloqueada")
    })
    public ResponseEntity<CuentaBancariaDTO> bloquearCuenta(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta) {
        
        CuentaBancaria cuenta = service.bloquearCuenta(numeroCuenta);
        return ResponseEntity.ok(mapper.toDTO(cuenta));
    }

    @PatchMapping("/numero/{numeroCuenta}/desbloquear")
    @Operation(summary = "Desbloquear cuenta", description = "Desbloquea una cuenta bancaria bloqueada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta desbloqueada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "400", description = "La cuenta no puede ser desbloqueada")
    })
    public ResponseEntity<CuentaBancariaDTO> desbloquearCuenta(
            @Parameter(description = "Número de cuenta") @PathVariable String numeroCuenta) {
        
        CuentaBancaria cuenta = service.desbloquearCuenta(numeroCuenta);
        return ResponseEntity.ok(mapper.toDTO(cuenta));
    }

    @GetMapping("/generar-numero")
    @Operation(summary = "Generar número de cuenta", description = "Genera un nuevo número de cuenta único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Número de cuenta generado exitosamente")
    })
    public ResponseEntity<String> generarNumeroCuenta() {
        String numeroCuenta = service.generarNumeroCuenta();
        return ResponseEntity.ok(numeroCuenta);
    }

    @ExceptionHandler({CuentaNotFoundException.class})
    public ResponseEntity<Void> handleCuentaNotFound(CuentaNotFoundException e) {
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
} 