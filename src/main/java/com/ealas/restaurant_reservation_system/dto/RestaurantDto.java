package com.ealas.restaurant_reservation_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantDto {

    @Size(max = 100)
    private String name;

    private String description;

    @Size(max = 12)
    private String phone;

    @Email
    private String email;

    private String address;

    @Size(max = 50)
    private String city;

    private Integer capacity;

    private String openingHours;

    @Size(max = 50)
    private String websiteUrl;
}
