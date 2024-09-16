package com.ealas.restaurant_reservation_system.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservationTableDto {
    private Long tableId;
    private Integer tableNumber;
    private String location;
}
