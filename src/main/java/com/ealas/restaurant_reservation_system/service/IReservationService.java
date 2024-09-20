package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.reservation.ReservationDetailsDto;
import com.ealas.restaurant_reservation_system.dto.reservation.ReservationDto;
import com.ealas.restaurant_reservation_system.dto.reservation.ReservationEventDto;
import com.ealas.restaurant_reservation_system.dto.reservation.ReservationTableDto;
import com.ealas.restaurant_reservation_system.entity.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IReservationService {
    List<ReservationDto> findAll();

    Optional<ReservationDto> findById(Long id);

    ReservationDto saveReservationTable(ReservationTableDto reservationDto);

    ReservationEventDto saveReservationEvent(ReservationEventDto reservationDto);

    Optional<ReservationDto> update(Long id, ReservationDto reservationDto);

    ReservationDetailsDto getReservationDetails(Long id);

    List<ReservationDto> findReservationsByDateRange(LocalDate startDate, LocalDate endDate);

    // Calcular las ganancias totales de una lista de reservas
    double calculateTotalRevenue(List<ReservationDto> reservations);
}
