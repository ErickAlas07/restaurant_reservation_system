package com.ealas.restaurant_reservation_system.dto.reservation;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DetailsTableDto {
    private Long tableId;
    private Integer tableNumber;
    private String location;
}
