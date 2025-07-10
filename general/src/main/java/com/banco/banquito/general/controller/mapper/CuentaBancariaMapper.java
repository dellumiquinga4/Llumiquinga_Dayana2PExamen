package com.banco.banquito.general.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banco.banquito.general.controller.dto.CuentaBancariaDTO;
import com.banco.banquito.general.controller.dto.CrearCuentaDTO;
import com.banco.banquito.general.model.CuentaBancaria;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CuentaBancariaMapper {

    CuentaBancariaDTO toDTO(CuentaBancaria model);
    
    CuentaBancaria toModel(CuentaBancariaDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaUltimaActualizacion", ignore = true)
    @Mapping(target = "saldoDisponible", ignore = true)
    @Mapping(target = "saldoContable", ignore = true)
    @Mapping(target = "diasInactividad", ignore = true)
    @Mapping(target = "permiteDebito", ignore = true)
    @Mapping(target = "permiteCredito", ignore = true)
    @Mapping(target = "generaExtractos", ignore = true)
    CuentaBancaria fromCrearCuentaDTO(CrearCuentaDTO dto);
} 