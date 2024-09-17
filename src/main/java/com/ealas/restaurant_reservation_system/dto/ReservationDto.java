package com.ealas.restaurant_reservation_system.dto;

import com.ealas.restaurant_reservation_system.entity.*;
import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
