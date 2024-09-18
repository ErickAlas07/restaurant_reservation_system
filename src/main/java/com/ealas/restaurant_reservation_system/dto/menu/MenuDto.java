package com.ealas.restaurant_reservation_system.dto.menu;

import com.ealas.restaurant_reservation_system.dto.restaurant.RestaurantDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private boolean available;
    private String category;
    private Long idRestaurant;
    private RestaurantDto restaurant;
}
