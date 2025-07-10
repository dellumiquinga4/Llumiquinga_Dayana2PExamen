package com.banco.banquito.general.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.banco.banquito.general.model.CuentaBancaria;

@Repository
public interface CuentaBancariaRepository extends MongoRepository<CuentaBancaria, String> {

    Optional<CuentaBancaria> findByNumeroCuenta(String numeroCuenta);

    List<CuentaBancaria> findByClienteIdentificacion(String clienteIdentificacion);

    Page<CuentaBancaria> findByClienteIdentificacion(String clienteIdentificacion, Pageable pageable);

    List<CuentaBancaria> findByClienteIdentificacionAndEstadoCuenta(String clienteIdentificacion, String estadoCuenta);

    Page<CuentaBancaria> findByClienteIdentificacionAndEstadoCuenta(String clienteIdentificacion, String estadoCuenta, Pageable pageable);

    List<CuentaBancaria> findByEstadoCuenta(String estadoCuenta);

    Page<CuentaBancaria> findByEstadoCuenta(String estadoCuenta, Pageable pageable);

    List<CuentaBancaria> findByTipoCuenta(String tipoCuenta);

    Page<CuentaBancaria> findByTipoCuenta(String tipoCuenta, Pageable pageable);

    List<CuentaBancaria> findByTipoCuentaAndEstadoCuenta(String tipoCuenta, String estadoCuenta);

    List<CuentaBancaria> findBySucursal(String sucursal);

    Page<CuentaBancaria> findBySucursal(String sucursal, Pageable pageable);

    List<CuentaBancaria> findByEjecutivo(String ejecutivo);

    Page<CuentaBancaria> findByEjecutivo(String ejecutivo, Pageable pageable);

    List<CuentaBancaria> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<CuentaBancaria> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    List<CuentaBancaria> findByDiasInactividadGreaterThan(Integer dias);

    List<CuentaBancaria> findByEstadoCuentaAndDiasInactividadGreaterThan(String estadoCuenta, Integer dias);

    boolean existsByNumeroCuenta(String numeroCuenta);

    boolean existsByClienteIdentificacionAndTipoCuenta(String clienteIdentificacion, String tipoCuenta);

    long countByEstadoCuenta(String estadoCuenta);

    long countByTipoCuenta(String tipoCuenta);

    long countByClienteIdentificacion(String clienteIdentificacion);
} 