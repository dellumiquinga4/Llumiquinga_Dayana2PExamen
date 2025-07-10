package com.banco.banquito.general.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banco.banquito.general.controller.dto.MovimientoCuentaDTO;
import com.banco.banquito.general.model.MovimientoCuenta;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MovimientoCuentaMapper {

    MovimientoCuentaDTO toDTO(MovimientoCuenta model);
    
    MovimientoCuenta toModel(MovimientoCuentaDTO dto);
} 