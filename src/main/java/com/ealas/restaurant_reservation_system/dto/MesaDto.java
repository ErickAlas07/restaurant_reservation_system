package com.ealas.restaurant_reservation_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MesaDto {
    private Long id;
    private Integer tableNumber;
    private String location;
    private Integer seats;
    private boolean available;
    private Long restaurantId;
}
