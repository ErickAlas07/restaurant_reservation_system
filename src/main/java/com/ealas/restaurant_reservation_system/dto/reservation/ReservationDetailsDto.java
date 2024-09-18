package com.ealas.restaurant_reservation_system.dto.reservation;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailsDto {
    private Long reservationId;
    private String reservationDate;
    private String occasion;
    private int people;
    private String notes;
    private List<ReservationTableDto> tables;
    private double totalPrice;
    private String status;
    private EventDto event;
}

