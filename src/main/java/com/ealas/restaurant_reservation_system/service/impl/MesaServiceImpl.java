package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.table.MesaDto;
import com.ealas.restaurant_reservation_system.entity.Mesa;
import com.ealas.restaurant_reservation_system.entity.Restaurant;
import com.ealas.restaurant_reservation_system.exceptions.ResourceAlreadyExistsException;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.IMesaRepository;
import com.ealas.restaurant_reservation_system.repository.IRestaurantRepository;
import com.ealas.restaurant_reservation_system.service.IMesaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MesaServiceImpl implements IMesaService {

    @Autowired
    IMesaRepository mesaRepository;

    @Autowired
    IRestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    @Override
    public List<MesaDto> findAll() {
        return mesaRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MesaDto> findById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id " + id));
        return Optional.of(toDto(mesa));
    }

    @Transactional
    @Override
    public MesaDto save(MesaDto mesaDTO) {
        Boolean exists = mesaRepository.existsByTableNumber(mesaDTO.getTableNumber());
        if (!exists) {
            Mesa mesa = toEntity(mesaDTO);
            Mesa mesaDb = mesaRepository.save(mesa);
            return toDto(mesaDb);
        } else {
            throw new ResourceAlreadyExistsException("Table with number :" + mesaDTO.getTableNumber() + " already exists.");
        }
    }

    @Transactional
    @Override
    public Optional<MesaDto> update(Long id, MesaDto mesaDTO) {
        Optional<Mesa> mesaOptional = mesaRepository.findById(id);
        if (mesaOptional.isPresent()) {
            Mesa mesaDb = mesaOptional.get();
            if (mesaDTO.getTableNumber() != null) mesaDb.setTableNumber(mesaDTO.getTableNumber());
            if (mesaDTO.getLocation() != null) mesaDb.setLocation(mesaDTO.getLocation());
            if (mesaDTO.getSeats() != null) mesaDb.setSeats(mesaDTO.getSeats());
            mesaDb.setAvailable(mesaDTO.isAvailable());

            if (mesaDTO.getRestaurantId() != null) {
                Restaurant restaurant = restaurantRepository.findById(mesaDTO.getRestaurantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + mesaDTO.getRestaurantId()));
                mesaDb.setRestaurant(restaurant);
            }
            Mesa mesaUpdated = mesaRepository.save(mesaDb);
            return Optional.of(toDto(mesaUpdated));
        } else {
            throw new ResourceNotFoundException("Table not found with id " + id);
        }
    }

    private MesaDto toDto(Mesa mesa) {
        MesaDto dto = new MesaDto();
        BeanUtils.copyProperties(mesa, dto);
        return dto;
    }

    private Mesa toEntity(MesaDto mesaDto) {
        Mesa mesa = new Mesa();
        mesa.setTableNumber(mesaDto.getTableNumber());
        mesa.setLocation(mesaDto.getLocation());
        mesa.setSeats(mesaDto.getSeats());
        mesa.setAvailable(mesaDto.isAvailable());

        if (mesaDto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(mesaDto.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + mesaDto.getRestaurantId()));
            mesa.setRestaurant(restaurant);

        }
        return mesa;
    }
}
