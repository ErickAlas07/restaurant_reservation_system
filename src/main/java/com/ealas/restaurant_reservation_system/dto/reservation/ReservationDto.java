package com.ealas.restaurant_reservation_system.dto.reservation;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Date reservationDate;
    private Integer people;
    private StatusReservation status;
    private SpecialOccasion occasion;
    private String notes;
    private Long eventId;
    private EventDto event;
}
