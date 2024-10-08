package com.ealas.restaurant_reservation_system.dto.reservation;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import com.ealas.restaurant_reservation_system.enums.ReservationType;
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
    private String reservationTime;
    private String occasion;
    private int people;
    private String notes;
    private List<DetailsTableDto> tables;
    private ReservationType reservationType;
    private double totalPrice;
    private String status;
    private EventDto event;

}

