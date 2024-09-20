package com.ealas.restaurant_reservation_system.dto.restaurant.mapper;

import com.ealas.restaurant_reservation_system.dto.restaurant.RestaurantDto;
import com.ealas.restaurant_reservation_system.entity.Restaurant;

public class RestaurantMapper {
    public static RestaurantDto toDTO(Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        dto.setCity(restaurant.getCity());
        dto.setCapacity(restaurant.getCapacity());
        dto.setOpeningHours(restaurant.getOpeningHours());
        dto.setWebsiteUrl(restaurant.getWebsiteUrl());
        return dto;
    }

    public static Restaurant toEntity(RestaurantDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhone(dto.getPhone());
        restaurant.setEmail(dto.getEmail());
        restaurant.setCity(dto.getCity());
        restaurant.setCapacity(dto.getCapacity());
        restaurant.setOpeningHours(dto.getOpeningHours());
        restaurant.setWebsiteUrl(dto.getWebsiteUrl());
        return restaurant;
    }
}
