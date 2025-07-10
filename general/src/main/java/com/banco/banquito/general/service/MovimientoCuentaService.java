package com.banco.banquito.general.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banco.banquito.general.controller.dto.MovimientoCuentaDTO;
import com.banco.banquito.general.exception.CuentaInactivaException;
import com.banco.banquito.general.exception.CuentaNotFoundException;
import com.banco.banquito.general.exception.SaldoInsuficienteException;
import com.banco.banquito.general.model.CuentaBancaria;
import com.banco.banquito.general.model.MovimientoCuenta;
import com.banco.banquito.general.repository.MovimientoCuentaRepository;

@Service
public class MovimientoCuentaService {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoCuentaService.class);

    private final MovimientoCuentaRepository repository;
    private final CuentaBancariaService cuentaService;

    public MovimientoCuentaService(MovimientoCuentaRepository repository, CuentaBancariaService cuentaService) {
        this.repository = repository;
        this.cuentaService = cuentaService;
    }

    public List<MovimientoCuenta> findAll() {
        logger.info("Consultando todos los movimientos de cuenta");
        return repository.findAll();
    }

    public Page<MovimientoCuenta> findAll(int page, int size, String sortBy, String sortDirection) {
        logger.info("Consultando movimientos de cuenta con paginación - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findAll(pageable);
    }

    public MovimientoCuenta findById(String id) {
        logger.info("Buscando movimiento de cuenta por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException(id, "ID de movimiento"));
    }

    public MovimientoCuenta findByNumeroComprobante(String numeroComprobante) {
        logger.info("Buscando movimiento por número de comprobante: {}", numeroComprobante);
        return repository.findByNumeroComprobante(numeroComprobante)
                .orElseThrow(() -> new CuentaNotFoundException(numeroComprobante, "Número de comprobante"));
    }

    public List<MovimientoCuenta> findByNumeroCuenta(String numeroCuenta) {
        logger.info("Buscando movimientos por número de cuenta: {}", numeroCuenta);
        return repository.findByNumeroCuenta(numeroCuenta);
    }

    public Page<MovimientoCuenta> findByNumeroCuenta(String numeroCuenta, int page, int size, String sortBy, String sortDirection) {
        logger.info("Buscando movimientos por número de cuenta con paginación - cuenta: {}, página: {}, tamaño: {}", 
                    numeroCuenta, page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findByNumeroCuenta(numeroCuenta, pageable);
    }

    public List<MovimientoCuenta> findByTipoMovimiento(String numeroCuenta, String tipoMovimiento) {
        logger.info("Buscando movimientos por tipo - cuenta: {}, tipo: {}", numeroCuenta, tipoMovimiento);
        return repository.findByNumeroCuentaAndTipoMovimiento(numeroCuenta, tipoMovimiento);
    }

    public Page<MovimientoCuenta> findByTipoMovimiento(String numeroCuenta, String tipoMovimiento, int page, int size, String sortBy, String sortDirection) {
        logger.info("Buscando movimientos por tipo con paginación - cuenta: {}, tipo: {}, página: {}, tamaño: {}", 
                    numeroCuenta, tipoMovimiento, page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findByNumeroCuentaAndTipoMovimiento(numeroCuenta, tipoMovimiento, pageable);
    }

    public List<MovimientoCuenta> findByRangoFechas(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        logger.info("Buscando movimientos por rango de fechas - cuenta: {}, desde: {}, hasta: {}", 
                    numeroCuenta, fechaInicio, fechaFin);
        return repository.findByNumeroCuentaAndFechaMovimientoBetween(numeroCuenta, fechaInicio, fechaFin);
    }

    public Page<MovimientoCuenta> findByRangoFechas(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                                                    int page, int size, String sortBy, String sortDirection) {
        logger.info("Buscando movimientos por rango de fechas con paginación - cuenta: {}, desde: {}, hasta: {}, página: {}, tamaño: {}", 
                    numeroCuenta, fechaInicio, fechaFin, page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findByNumeroCuentaAndFechaMovimientoBetween(numeroCuenta, fechaInicio, fechaFin, pageable);
    }

    public MovimientoCuenta procesarMovimiento(MovimientoCuentaDTO movimientoDTO) {
        logger.info("Procesando movimiento - cuenta: {}, tipo: {}, monto: {}", 
                    movimientoDTO.getNumeroCuenta(), movimientoDTO.getTipoMovimiento(), movimientoDTO.getMonto());
        
        // Validar que el número de comprobante no exista
        if (repository.existsByNumeroComprobante(movimientoDTO.getNumeroComprobante())) {
            throw new RuntimeException("Ya existe un movimiento con el número de comprobante: " + movimientoDTO.getNumeroComprobante());
        }
        
        // Validar que la cuenta exista y esté activa
        CuentaBancaria cuenta = cuentaService.findByNumeroCuenta(movimientoDTO.getNumeroCuenta());
        if (!"ACTIVA".equals(cuenta.getEstadoCuenta())) {
            throw new CuentaInactivaException(movimientoDTO.getNumeroCuenta(), cuenta.getEstadoCuenta());
        }
        
        // Validar disponibilidad de saldo para débitos
        boolean esDebito = "DEBITO".equals(movimientoDTO.getTipoMovimiento());
        if (esDebito && !cuenta.puedeRealizarDebito(movimientoDTO.getMonto())) {
            throw new SaldoInsuficienteException(movimientoDTO.getNumeroCuenta(), cuenta.getSaldoDisponible(), movimientoDTO.getMonto());
        }
        
        // Crear el movimiento
        MovimientoCuenta movimiento = new MovimientoCuenta(
            movimientoDTO.getNumeroCuenta(),
            movimientoDTO.getNumeroComprobante(),
            movimientoDTO.getTipoMovimiento(),
            movimientoDTO.getMonto(),
            cuenta.getSaldoDisponible(),
            movimientoDTO.getConcepto()
        );
        
        // Asignar valores opcionales
        if (movimientoDTO.getDescripcion() != null) {
            movimiento.setDescripcion(movimientoDTO.getDescripcion());
        }
        
        if (movimientoDTO.getSucursal() != null) {
            movimiento.setSucursal(movimientoDTO.getSucursal());
        }
        
        if (movimientoDTO.getCajero() != null) {
            movimiento.setCajero(movimientoDTO.getCajero());
        }
        
        if (movimientoDTO.getCanalTransaccion() != null) {
            movimiento.setCanalTransaccion(movimientoDTO.getCanalTransaccion());
        }
        
        if (movimientoDTO.getReferenciaExterna() != null) {
            movimiento.setReferenciaExterna(movimientoDTO.getReferenciaExterna());
        }
        
        if (movimientoDTO.getObservaciones() != null) {
            movimiento.setObservaciones(movimientoDTO.getObservaciones());
        }
        
        // Procesar el movimiento
        movimiento.procesar();
        
        // Actualizar los saldos de la cuenta
        cuentaService.actualizarSaldos(movimientoDTO.getNumeroCuenta(), movimientoDTO.getMonto(), esDebito);
        
        // Guardar el movimiento
        MovimientoCuenta movimientoGuardado = repository.save(movimiento);
        logger.info("Movimiento procesado exitosamente - ID: {}, Comprobante: {}", 
                    movimientoGuardado.getId(), movimientoGuardado.getNumeroComprobante());
        
        return movimientoGuardado;
    }

    public MovimientoCuenta reversarMovimiento(String numeroComprobante, String motivo) {
        logger.info("Reversando movimiento con comprobante: {}, motivo: {}", numeroComprobante, motivo);
        
        MovimientoCuenta movimientoOriginal = findByNumeroComprobante(numeroComprobante);
        
        if (movimientoOriginal.getReversado()) {
            throw new RuntimeException("El movimiento ya está reversado");
        }
        
        // Crear el movimiento reverso
        String tipoMovimientoReverso = "DEBITO".equals(movimientoOriginal.getTipoMovimiento()) ? "CREDITO" : "DEBITO";
        String numeroComprobanteReverso = generarNumeroComprobante();
        
        // Obtener saldo actual de la cuenta
        BigDecimal saldoActual = cuentaService.consultarSaldoDisponible(movimientoOriginal.getNumeroCuenta());
        
        MovimientoCuenta movimientoReverso = new MovimientoCuenta(
            movimientoOriginal.getNumeroCuenta(),
            numeroComprobanteReverso,
            tipoMovimientoReverso,
            movimientoOriginal.getMonto(),
            saldoActual,
            "REVERSO - " + movimientoOriginal.getConcepto()
        );
        
        movimientoReverso.setDescripcion("Reverso del movimiento: " + numeroComprobante + " - " + motivo);
        movimientoReverso.setReferenciaExterna(numeroComprobante);
        movimientoReverso.setObservaciones("Reverso de movimiento original");
        movimientoReverso.procesar();
        
        // Actualizar los saldos de la cuenta
        boolean esDebito = "DEBITO".equals(tipoMovimientoReverso);
        cuentaService.actualizarSaldos(movimientoOriginal.getNumeroCuenta(), movimientoOriginal.getMonto(), esDebito);
        
        // Guardar el movimiento reverso
        MovimientoCuenta movimientoReversoGuardado = repository.save(movimientoReverso);
        
        // Marcar el movimiento original como reversado
        movimientoOriginal.reversar(movimientoReversoGuardado.getId());
        repository.save(movimientoOriginal);
        
        logger.info("Movimiento reversado exitosamente - Original: {}, Reverso: {}", 
                    numeroComprobante, numeroComprobanteReverso);
        
        return movimientoReversoGuardado;
    }

    public boolean existeNumeroComprobante(String numeroComprobante) {
        return repository.existsByNumeroComprobante(numeroComprobante);
    }

    public String generarNumeroComprobante() {
        String numeroComprobante;
        do {
            numeroComprobante = "COMP-" + LocalDateTime.now().getYear() + "-" + 
                               String.format("%06d", Math.abs(UUID.randomUUID().hashCode() % 1000000));
        } while (existeNumeroComprobante(numeroComprobante));
        
        logger.info("Número de comprobante generado: {}", numeroComprobante);
        return numeroComprobante;
    }

    public long contarMovimientos(String numeroCuenta) {
        return repository.countByNumeroCuenta(numeroCuenta);
    }

    public long contarMovimientosPorTipo(String numeroCuenta, String tipoMovimiento) {
        return repository.countByNumeroCuentaAndTipoMovimiento(numeroCuenta, tipoMovimiento);
    }

    public MovimientoCuenta obtenerUltimoMovimiento(String numeroCuenta) {
        return repository.findTopByNumeroCuentaOrderByFechaMovimientoDesc(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta, "Movimientos para la cuenta"));
    }

    public List<MovimientoCuenta> obtenerMovimientosOrdenados(String numeroCuenta) {
        return repository.findByNumeroCuentaOrderByFechaMovimientoDesc(numeroCuenta);
    }

    public Page<MovimientoCuenta> obtenerMovimientosOrdenados(String numeroCuenta, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByNumeroCuentaOrderByFechaMovimientoDesc(numeroCuenta, pageable);
    }
} 