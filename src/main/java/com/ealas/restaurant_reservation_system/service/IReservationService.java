package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.ReservationDetailsDto;
import com.ealas.restaurant_reservation_system.dto.ReservationDto;
import com.ealas.restaurant_reservation_system.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface IReservationService {
    List<ReservationDto> findAll();

    Optional<ReservationDto> findById(Long id);

    ReservationDto save(ReservationDto reservationDto);

    Optional<ReservationDto> update(Long id, ReservationDto reservationDto);

    ReservationDetailsDto getReservationDetails(Long id);
}
