package com.ealas.restaurant_reservation_system.dto.reservation;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer people;
    private StatusReservation status;
    private SpecialOccasion occasion;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ReservationType reservationType;

    private String notes;
    private Long eventId;
    private EventDto event;

    private List<ReservationDetailsDto> reservationDetails; // Lista de detalles de la reserva
    private double totalAmount; // Total de la reserva
}
