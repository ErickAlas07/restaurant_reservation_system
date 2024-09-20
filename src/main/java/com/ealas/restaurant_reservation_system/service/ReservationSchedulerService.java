package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.Reservation;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.ealas.restaurant_reservation_system.repository.IReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReservationSchedulerService {

    private final IReservationRepository reservationRepository;

    @Autowired
    public ReservationSchedulerService(IReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelUnconfirmedReservations() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("America/El_Salvador"));

        // Buscar reservas pendientes con fecha de reserva vencida
        List<Reservation> unconfirmedReservations = reservationRepository.findPendingReservations(currentDate);

        for (Reservation reservation : unconfirmedReservations) {
            reservation.setStatus(StatusReservation.CANCELED);
            reservationRepository.save(reservation);
            System.out.println("Reservation with ID " + reservation.getId() + " has been cancelled.");
        }
    }
}
