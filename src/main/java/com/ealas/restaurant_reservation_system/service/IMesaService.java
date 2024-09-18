package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.table.MesaDto;
import com.ealas.restaurant_reservation_system.entity.Mesa;

import java.util.List;
import java.util.Optional;

public interface IMesaService {
    List<MesaDto> findAll();

    Optional<MesaDto> findById(Long id);

    MesaDto save(MesaDto mesaDTO);

    Optional<MesaDto> update(Long id, MesaDto mesaDTO);
}
