package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.Mesa;
import com.ealas.restaurant_reservation_system.entity.Reservation;
import com.ealas.restaurant_reservation_system.entity.ReservationDetails;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.ealas.restaurant_reservation_system.repository.IMesaRepository;
import com.ealas.restaurant_reservation_system.repository.IReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReservationSchedulerService {

    private final IReservationRepository reservationRepository;
    private final IMesaRepository mesaRepository;

    @Autowired
    public ReservationSchedulerService(IReservationRepository reservationRepository, IMesaRepository iMesaRepository) {
        this.reservationRepository = reservationRepository;
        this.mesaRepository = iMesaRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelUnconfirmedReservations() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("America/El_Salvador"));

        // Buscar reservas pendientes con fecha de reserva vencida
        List<Reservation> unconfirmedReservations = reservationRepository.findPendingReservations(currentDate);

        for (Reservation reservation : unconfirmedReservations) {
            reservation.setStatus(StatusReservation.CANCELED);
            reservationRepository.save(reservation);
            System.out.println("Reservación con ID " + reservation.getId() + " ha sido cancelada.");
        }
    }

    @Scheduled(fixedRate = 3600000) // Se ejecuta cada hora
    public void releaseTablesAfterReservation() {
        LocalDate twoHoursAgoDate = LocalDate.now(ZoneId.of("America/El_Salvador"));
        LocalTime twoHoursAgoTime = LocalTime.now(ZoneId.of("America/El_Salvador")).minusHours(2);

        // Buscar todas las reservaciones que finalizaron hace más de 2 horas
        List<Reservation> finishedReservations = reservationRepository.findReservationsEndedBefore(twoHoursAgoDate, twoHoursAgoTime);

        for (Reservation reservation : finishedReservations) {
            for (ReservationDetails details : reservation.getReservationDetails()) {
                Mesa table = details.getTable(); // Obtener la mesa desde los detalles de la reservación
                if (table != null && !table.isAvailable()) {
                    table.setAvailable(true); // Marcar mesa como disponible
                    mesaRepository.save(table);
                    System.out.println("Mesa número: " + table.getTableNumber() + " ha sido puesta como disponible.");
                }
            }
        }
    }

}
