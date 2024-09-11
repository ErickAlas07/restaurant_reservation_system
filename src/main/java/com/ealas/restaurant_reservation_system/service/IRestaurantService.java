package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.RestaurantDto;
import com.ealas.restaurant_reservation_system.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface IRestaurantService {
    List<RestaurantDto> findAll();

    Optional<RestaurantDto> findById(Long id);

    RestaurantDto save(RestaurantDto restaurant);

    Optional<RestaurantDto> update(Long id, RestaurantDto restaurant);

    Optional<RestaurantDto> delete(Long id);
}
