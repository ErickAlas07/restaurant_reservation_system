package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.RestaurantDto;
import com.ealas.restaurant_reservation_system.entity.Restaurant;
import com.ealas.restaurant_reservation_system.mapper.RestaurantMapper;
import com.ealas.restaurant_reservation_system.repository.IRestaurantRepository;
import com.ealas.restaurant_reservation_system.service.IRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements IRestaurantService {

    @Autowired
    IRestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    @Override
    public List<RestaurantDto> findAll() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RestaurantDto> findById(Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        return restaurant.map(RestaurantMapper::toDTO);
    }

    @Transactional
    @Override
    public RestaurantDto save(RestaurantDto restaurantDTO) {
        boolean exists = restaurantRepository.existsByName(restaurantDTO.getName());
        if(!exists) {
            Restaurant restaurant = RestaurantMapper.toEntity(restaurantDTO);
            Restaurant restaurantDb = restaurantRepository.save(restaurant);
            return RestaurantMapper.toDTO(restaurantDb);
        } else {
            throw new RuntimeException("Restaurant already exists");
        }
    }

    @Transactional
    @Override
    public Optional<RestaurantDto> update(Long id, RestaurantDto restaurantDTO) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isPresent()) {
            Restaurant restaurantdb = restaurantOptional.get();
            if (restaurantDTO.getName() != null) restaurantdb.setName(restaurantDTO.getName());
            if (restaurantDTO.getDescription() != null) restaurantdb.setDescription(restaurantDTO.getDescription());
            if (restaurantDTO.getPhone() != null) restaurantdb.setPhone(restaurantDTO.getPhone());
            if (restaurantDTO.getEmail() != null) restaurantdb.setEmail(restaurantDTO.getEmail());
            if (restaurantDTO.getCity() != null) restaurantdb.setCity(restaurantDTO.getCity());
            if (restaurantDTO.getCapacity() != null) restaurantdb.setCapacity(restaurantDTO.getCapacity());
            if (restaurantDTO.getOpeningHours() != null) restaurantdb.setOpeningHours(restaurantDTO.getOpeningHours());
            if (restaurantDTO.getWebsiteUrl() != null) restaurantdb.setWebsiteUrl(restaurantDTO.getWebsiteUrl());

            Restaurant updatedRestaurant = restaurantRepository.save(restaurantdb);
            return Optional.of(RestaurantMapper.toDTO(updatedRestaurant));
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public Optional<RestaurantDto> delete(Long id) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isPresent()) {
            restaurantRepository.delete(restaurantOptional.get());
            return Optional.of(RestaurantMapper.toDTO(restaurantOptional.get()));
        }
        return Optional.empty();
    }
}
