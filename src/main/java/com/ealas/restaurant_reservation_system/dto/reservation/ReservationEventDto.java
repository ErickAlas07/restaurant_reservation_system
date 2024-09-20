package com.ealas.restaurant_reservation_system.dto.reservation;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
public class ReservationEventDto {

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    @NotNull
    private Integer people;

    private StatusReservation status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ReservationType reservationType;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long eventId;

    private EventDto event;
}
