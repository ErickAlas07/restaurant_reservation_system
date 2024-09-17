package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.RestaurantDto;
import com.ealas.restaurant_reservation_system.entity.Restaurant;
import com.ealas.restaurant_reservation_system.exceptions.ResourceAlreadyExistsException;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
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
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found."));
        return Optional.of(RestaurantMapper.toDTO(restaurant));
    }

    @Transactional
    @Override
    public RestaurantDto save(RestaurantDto restaurantDTO) {
        boolean exists = restaurantRepository.existsByName(restaurantDTO.getName());
        if (exists) {
            throw new ResourceAlreadyExistsException("Restaurant with name " + restaurantDTO.getName() + " already exists.");
        }
        Restaurant restaurant = RestaurantMapper.toEntity(restaurantDTO);
        Restaurant restaurantDb = restaurantRepository.save(restaurant);
        return RestaurantMapper.toDTO(restaurantDb);
    }

    @Transactional
    @Override
    public Optional<RestaurantDto> update(Long id, RestaurantDto restaurantDTO) {
        Restaurant restaurantdb = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found."));

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

    @Transactional
    @Override
    public Optional<RestaurantDto> delete(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found."));

        restaurantRepository.delete(restaurant);
        return Optional.of(RestaurantMapper.toDTO(restaurant));
    }
}
