package com.banco.banquito.general.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.banco.banquito.general.model.MovimientoCuenta;

@Repository
public interface MovimientoCuentaRepository extends MongoRepository<MovimientoCuenta, String> {

    Optional<MovimientoCuenta> findByNumeroComprobante(String numeroComprobante);

    List<MovimientoCuenta> findByNumeroCuenta(String numeroCuenta);

    Page<MovimientoCuenta> findByNumeroCuenta(String numeroCuenta, Pageable pageable);

    List<MovimientoCuenta> findByNumeroCuentaAndTipoMovimiento(String numeroCuenta, String tipoMovimiento);

    Page<MovimientoCuenta> findByNumeroCuentaAndTipoMovimiento(String numeroCuenta, String tipoMovimiento, Pageable pageable);

    List<MovimientoCuenta> findByNumeroCuentaAndFechaMovimientoBetween(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<MovimientoCuenta> findByNumeroCuentaAndFechaMovimientoBetween(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    List<MovimientoCuenta> findByNumeroCuentaAndTipoMovimientoAndFechaMovimientoBetween(String numeroCuenta, String tipoMovimiento, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<MovimientoCuenta> findByNumeroCuentaAndTipoMovimientoAndFechaMovimientoBetween(String numeroCuenta, String tipoMovimiento, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    List<MovimientoCuenta> findByNumeroCuentaAndMontoGreaterThan(String numeroCuenta, BigDecimal monto);

    List<MovimientoCuenta> findByNumeroCuentaAndMontoBetween(String numeroCuenta, BigDecimal montoMinimo, BigDecimal montoMaximo);

    List<MovimientoCuenta> findByNumeroCuentaAndProcesado(String numeroCuenta, Boolean procesado);

    Page<MovimientoCuenta> findByNumeroCuentaAndProcesado(String numeroCuenta, Boolean procesado, Pageable pageable);

    List<MovimientoCuenta> findByNumeroCuentaAndReversado(String numeroCuenta, Boolean reversado);

    Page<MovimientoCuenta> findByNumeroCuentaAndReversado(String numeroCuenta, Boolean reversado, Pageable pageable);

    List<MovimientoCuenta> findByNumeroCuentaAndConceptoContainingIgnoreCase(String numeroCuenta, String concepto);

    Page<MovimientoCuenta> findByNumeroCuentaAndConceptoContainingIgnoreCase(String numeroCuenta, String concepto, Pageable pageable);

    List<MovimientoCuenta> findBySucursal(String sucursal);

    Page<MovimientoCuenta> findBySucursal(String sucursal, Pageable pageable);

    List<MovimientoCuenta> findByCanalTransaccion(String canalTransaccion);

    Page<MovimientoCuenta> findByCanalTransaccion(String canalTransaccion, Pageable pageable);

    List<MovimientoCuenta> findByFechaMovimientoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<MovimientoCuenta> findByFechaMovimientoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    boolean existsByNumeroComprobante(String numeroComprobante);

    boolean existsByNumeroCuentaAndNumeroComprobante(String numeroCuenta, String numeroComprobante);

    long countByNumeroCuenta(String numeroCuenta);

    long countByNumeroCuentaAndTipoMovimiento(String numeroCuenta, String tipoMovimiento);

    long countByNumeroCuentaAndFechaMovimientoBetween(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Método para obtener el último movimiento de una cuenta (para validaciones)
    Optional<MovimientoCuenta> findTopByNumeroCuentaOrderByFechaMovimientoDesc(String numeroCuenta);

    // Método para obtener movimientos ordenados por fecha descendente
    List<MovimientoCuenta> findByNumeroCuentaOrderByFechaMovimientoDesc(String numeroCuenta);

    Page<MovimientoCuenta> findByNumeroCuentaOrderByFechaMovimientoDesc(String numeroCuenta, Pageable pageable);
} 