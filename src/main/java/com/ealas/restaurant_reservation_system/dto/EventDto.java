package com.ealas.restaurant_reservation_system.dto;

import com.ealas.restaurant_reservation_system.entity.Restaurant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private String title;

    private String description;

    private Date eventDate;

    private Double ticketPrice;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer capacity;
}
