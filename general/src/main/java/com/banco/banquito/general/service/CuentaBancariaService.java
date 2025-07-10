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

import com.banco.banquito.general.controller.dto.CrearCuentaDTO;
import com.banco.banquito.general.exception.CuentaInactivaException;
import com.banco.banquito.general.exception.CuentaNotFoundException;
import com.banco.banquito.general.exception.SaldoInsuficienteException;
import com.banco.banquito.general.model.CuentaBancaria;
import com.banco.banquito.general.repository.CuentaBancariaRepository;

@Service
public class CuentaBancariaService {

    private static final Logger logger = LoggerFactory.getLogger(CuentaBancariaService.class);

    private final CuentaBancariaRepository repository;

    public CuentaBancariaService(CuentaBancariaRepository repository) {
        this.repository = repository;
    }

    public List<CuentaBancaria> findAll() {
        logger.info("Consultando todas las cuentas bancarias");
        return repository.findAll();
    }

    public Page<CuentaBancaria> findAll(int page, int size, String sortBy, String sortDirection) {
        logger.info("Consultando cuentas bancarias con paginación - página: {}, tamaño: {}, ordenar por: {}, dirección: {}", 
                    page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findAll(pageable);
    }

    public CuentaBancaria findById(String id) {
        logger.info("Buscando cuenta bancaria por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException(id, "ID"));
    }

    public CuentaBancaria findByNumeroCuenta(String numeroCuenta) {
        logger.info("Buscando cuenta bancaria por número: {}", numeroCuenta);
        return repository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta, "Número de cuenta"));
    }

    public List<CuentaBancaria> findByClienteIdentificacion(String clienteIdentificacion) {
        logger.info("Buscando cuentas bancarias por cliente: {}", clienteIdentificacion);
        return repository.findByClienteIdentificacion(clienteIdentificacion);
    }

    public Page<CuentaBancaria> findByClienteIdentificacion(String clienteIdentificacion, int page, int size, String sortBy, String sortDirection) {
        logger.info("Buscando cuentas bancarias por cliente con paginación - cliente: {}, página: {}, tamaño: {}", 
                    clienteIdentificacion, page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findByClienteIdentificacion(clienteIdentificacion, pageable);
    }

    public List<CuentaBancaria> findByEstadoCuenta(String estadoCuenta) {
        logger.info("Buscando cuentas bancarias por estado: {}", estadoCuenta);
        return repository.findByEstadoCuenta(estadoCuenta);
    }

    public Page<CuentaBancaria> findByEstadoCuenta(String estadoCuenta, int page, int size, String sortBy, String sortDirection) {
        logger.info("Buscando cuentas bancarias por estado con paginación - estado: {}, página: {}, tamaño: {}", 
                    estadoCuenta, page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return repository.findByEstadoCuenta(estadoCuenta, pageable);
    }

    public List<CuentaBancaria> findByTipoCuenta(String tipoCuenta) {
        logger.info("Buscando cuentas bancarias por tipo: {}", tipoCuenta);
        return repository.findByTipoCuenta(tipoCuenta);
    }

    public CuentaBancaria crearCuenta(CrearCuentaDTO crearCuentaDTO) {
        logger.info("Creando nueva cuenta bancaria para cliente: {}", crearCuentaDTO.getClienteIdentificacion());
        
        // Validar que el número de cuenta no exista
        if (repository.existsByNumeroCuenta(crearCuentaDTO.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + crearCuentaDTO.getNumeroCuenta());
        }
        
        // Validar regla de negocio: máximo 5 cuentas por cliente
        long cuentasCliente = repository.countByClienteIdentificacion(crearCuentaDTO.getClienteIdentificacion());
        if (cuentasCliente >= 5) {
            throw new RuntimeException("El cliente ya tiene el máximo de 5 cuentas permitidas");
        }
        
        // Validar regla de negocio: solo una cuenta de plazo fijo por cliente
        if ("PLAZO_FIJO".equals(crearCuentaDTO.getTipoCuenta())) {
            if (repository.existsByClienteIdentificacionAndTipoCuenta(crearCuentaDTO.getClienteIdentificacion(), "PLAZO_FIJO")) {
                throw new RuntimeException("El cliente ya tiene una cuenta de plazo fijo");
            }
        }
        
        // Crear la cuenta
        CuentaBancaria cuenta = new CuentaBancaria(
            crearCuentaDTO.getNumeroCuenta(),
            crearCuentaDTO.getClienteIdentificacion(),
            crearCuentaDTO.getClienteNombre(),
            crearCuentaDTO.getTipoCuenta()
        );
        
        // Asignar valores opcionales
        if (crearCuentaDTO.getSaldoInicial() != null) {
            cuenta.setSaldoDisponible(crearCuentaDTO.getSaldoInicial());
            cuenta.setSaldoContable(crearCuentaDTO.getSaldoInicial());
        }
        
        if (crearCuentaDTO.getLimiteSobregiro() != null) {
            cuenta.setLimiteSobregiro(crearCuentaDTO.getLimiteSobregiro());
        }
        
        if (crearCuentaDTO.getMoneda() != null) {
            cuenta.setMoneda(crearCuentaDTO.getMoneda());
        }
        
        if (crearCuentaDTO.getSucursal() != null) {
            cuenta.setSucursal(crearCuentaDTO.getSucursal());
        }
        
        if (crearCuentaDTO.getEjecutivo() != null) {
            cuenta.setEjecutivo(crearCuentaDTO.getEjecutivo());
        }
        
        CuentaBancaria cuentaGuardada = repository.save(cuenta);
        logger.info("Cuenta bancaria creada exitosamente: {}", cuentaGuardada.getNumeroCuenta());
        
        return cuentaGuardada;
    }

    public CuentaBancaria actualizarSaldos(String numeroCuenta, BigDecimal monto, boolean esDebito) {
        logger.info("Actualizando saldos de cuenta: {} - Monto: {} - Es débito: {}", numeroCuenta, monto, esDebito);
        
        CuentaBancaria cuenta = findByNumeroCuenta(numeroCuenta);
        
        if (!"ACTIVA".equals(cuenta.getEstadoCuenta())) {
            throw new CuentaInactivaException(numeroCuenta, cuenta.getEstadoCuenta());
        }
        
        if (esDebito && !cuenta.puedeRealizarDebito(monto)) {
            throw new SaldoInsuficienteException(numeroCuenta, cuenta.getSaldoDisponible(), monto);
        }
        
        if (!esDebito && !cuenta.puedeRealizarCredito()) {
            throw new CuentaInactivaException(numeroCuenta, cuenta.getEstadoCuenta());
        }
        
        cuenta.actualizarSaldos(monto, esDebito);
        
        CuentaBancaria cuentaActualizada = repository.save(cuenta);
        logger.info("Saldos actualizados exitosamente para cuenta: {}", numeroCuenta);
        
        return cuentaActualizada;
    }

    public CuentaBancaria bloquearCuenta(String numeroCuenta) {
        logger.info("Bloqueando cuenta: {}", numeroCuenta);
        
        CuentaBancaria cuenta = findByNumeroCuenta(numeroCuenta);
        
        if (!"ACTIVA".equals(cuenta.getEstadoCuenta())) {
            throw new RuntimeException("Solo se pueden bloquear cuentas activas");
        }
        
        cuenta.setEstadoCuenta("BLOQUEADA");
        cuenta.setFechaUltimaActualizacion(LocalDateTime.now());
        
        CuentaBancaria cuentaActualizada = repository.save(cuenta);
        logger.info("Cuenta bloqueada exitosamente: {}", numeroCuenta);
        
        return cuentaActualizada;
    }

    public CuentaBancaria desbloquearCuenta(String numeroCuenta) {
        logger.info("Desbloqueando cuenta: {}", numeroCuenta);
        
        CuentaBancaria cuenta = findByNumeroCuenta(numeroCuenta);
        
        if (!"BLOQUEADA".equals(cuenta.getEstadoCuenta())) {
            throw new RuntimeException("Solo se pueden desbloquear cuentas bloqueadas");
        }
        
        cuenta.setEstadoCuenta("ACTIVA");
        cuenta.setFechaUltimaActualizacion(LocalDateTime.now());
        
        CuentaBancaria cuentaActualizada = repository.save(cuenta);
        logger.info("Cuenta desbloqueada exitosamente: {}", numeroCuenta);
        
        return cuentaActualizada;
    }

    public void validarCuentaActiva(String numeroCuenta) {
        logger.info("Validando que la cuenta esté activa: {}", numeroCuenta);
        
        CuentaBancaria cuenta = findByNumeroCuenta(numeroCuenta);
        
        if (!"ACTIVA".equals(cuenta.getEstadoCuenta())) {
            throw new CuentaInactivaException(numeroCuenta, cuenta.getEstadoCuenta());
        }
    }

    public BigDecimal consultarSaldoDisponible(String numeroCuenta) {
        logger.info("Consultando saldo disponible para cuenta: {}", numeroCuenta);
        
        CuentaBancaria cuenta = findByNumeroCuenta(numeroCuenta);
        return cuenta.getSaldoDisponible();
    }

    public boolean existeNumeroCuenta(String numeroCuenta) {
        return repository.existsByNumeroCuenta(numeroCuenta);
    }

    public String generarNumeroCuenta() {
        String numeroCuenta;
        do {
            numeroCuenta = String.format("%010d", Math.abs(UUID.randomUUID().hashCode() % 1000000000));
        } while (existeNumeroCuenta(numeroCuenta));
        
        logger.info("Número de cuenta generado: {}", numeroCuenta);
        return numeroCuenta;
    }
} 