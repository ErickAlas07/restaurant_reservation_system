package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.reservation.ReservationDto;
import com.ealas.restaurant_reservation_system.dto.reservation.ReservationTableDto;
import com.ealas.restaurant_reservation_system.entity.Reservation;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.ealas.restaurant_reservation_system.exceptions.reservation.ReservationFailedException;
import com.ealas.restaurant_reservation_system.repository.IReservationRepository;
import com.ealas.restaurant_reservation_system.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

    @Mock
    private IReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private ReservationTableDto reservationDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationDto = new ReservationTableDto();
        reservationDto.setReservationDate(LocalDate.of(2024, 12, 24));
        reservationDto.setReservationTime(LocalTime.of(19, 0));
        reservationDto.setPeople(3);
        reservationDto.setOccasion(SpecialOccasion.valueOf("CUMPLEAÑOS"));
        reservationDto.setNotes("Request a window seat");
        // Configura otros campos necesarios...
    }

    @Test
    public void testCreateReservation() {
        Reservation reservation = new Reservation();
        // Configura la entidad de reserva...

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Optional<ReservationDto> createdReservation = Optional.ofNullable(reservationService.saveReservationTable(reservationDto));

        assertTrue(createdReservation.isPresent());
        assertEquals(reservationDto.getOccasion(), createdReservation.get().getOccasion());
    }

    @Test
    public void testDuplicateReservationNotAllowed() {
        Long userId = 1L;
        LocalDate date = LocalDate.of(2024, 12, 24);
        LocalTime time = LocalTime.of(19, 0);

        when(reservationRepository.existsByUserIdAndReservationDateAndReservationTime(userId, date, time)).thenReturn(true);

        Exception exception = assertThrows(ReservationFailedException.class, () -> {
            reservationService.saveReservationTable(reservationDto);
        });

        assertEquals("Ya tienes una reserva para esta fecha y hora.", exception.getMessage());
    }
}
