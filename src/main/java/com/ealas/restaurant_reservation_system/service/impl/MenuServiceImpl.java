package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.restaurant.RestaurantDto;
import com.ealas.restaurant_reservation_system.dto.menu.MenuDto;
import com.ealas.restaurant_reservation_system.entity.Menu;
import com.ealas.restaurant_reservation_system.entity.Restaurant;
import com.ealas.restaurant_reservation_system.exceptions.ResourceAlreadyExistsException;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.IMenuRepository;
import com.ealas.restaurant_reservation_system.repository.IRestaurantRepository;
import com.ealas.restaurant_reservation_system.service.IMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements IMenuService {

    @Autowired
    IMenuRepository menuRepository;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    @Override
    public List<MenuDto> findAll() {
        return menuRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MenuDto> findById(Long id) {
            Menu menu = menuRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id " + id));
            return Optional.of(convertToDTO(menu));
    }

    @Transactional
    @Override
    public MenuDto save(MenuDto menuDto) {
        Optional<Menu> exists = menuRepository.findByName(menuDto.getName());
        if (exists.isPresent()) {
            throw new ResourceAlreadyExistsException("Menu with name: " + menuDto.getName() + " already exists.");
        }
        Menu menu = toEntity(menuDto);
        Restaurant restaurant = restaurantRepository.findById(menuDto.getIdRestaurant())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + menuDto.getIdRestaurant() + " not found."));
        menu.setRestaurant(restaurant);

        Menu menuDb = menuRepository.save(menu);
        return convertToDTO(menuDb);
    }

    @Transactional
    @Override
    public Optional<MenuDto> update(Long id, MenuDto menuDto) {
        Optional<Menu> menu = menuRepository.findById(id);
        if (menu.isPresent()) {
            Menu menuDb = menu.get();
            if (menuDto.getName() != null) menuDb.setName(menuDto.getName());
            if (menuDto.getDescription() != null) menuDb.setDescription(menuDto.getDescription());
            if (menuDto.getPrice() != null) menuDb.setPrice(menuDto.getPrice());
            if (menuDto.getCategory() != null) menuDb.setCategory(menuDto.getCategory());
            if (menuDto.getIdRestaurant() != null) {
                Restaurant restaurant = restaurantRepository.findById(menuDto.getIdRestaurant())
                        .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + menuDto.getIdRestaurant() + " not found."));
                menuDb.setRestaurant(restaurant);
            }
            Menu menuUpdated = menuRepository.save(menuDb);
            return Optional.of(convertToDTO(menuUpdated));
        } else {
            throw new ResourceNotFoundException("Menu not found with id " + id);
        }
    }

    @Override
    public List<MenuDto> findAllAvailable() {
        return menuRepository.findByAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MenuDto convertToDTO(Menu menu) {
        MenuDto dto = new MenuDto();
        BeanUtils.copyProperties(menu, dto);

        // Si el restaurante está presente, asignar el idRestaurant y crear el RestaurantDto con los datos correspondientes
        if (menu.getRestaurant() != null) {
            dto.setIdRestaurant(menu.getRestaurant().getId());

            RestaurantDto restaurantDto = new RestaurantDto();
            restaurantDto.setName(menu.getRestaurant().getName());
            restaurantDto.setDescription(menu.getRestaurant().getDescription());
            restaurantDto.setPhone(menu.getRestaurant().getPhone());
            restaurantDto.setEmail(menu.getRestaurant().getEmail());
            restaurantDto.setAddress(menu.getRestaurant().getAddress());
            restaurantDto.setCity(menu.getRestaurant().getCity());
            restaurantDto.setOpeningHours(menu.getRestaurant().getOpeningHours());
            restaurantDto.setWebsiteUrl(menu.getRestaurant().getWebsiteUrl());

            dto.setRestaurant(restaurantDto);
        }
        return dto;
    }

    private Menu toEntity(MenuDto menuDto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDto, menu);
        if (menuDto.getIdRestaurant() != null) {
            Restaurant restaurant = restaurantRepository.findById(menuDto.getIdRestaurant())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id " + menuDto.getIdRestaurant()));
            menu.setRestaurant(restaurant);
        }
        return menu;
    }
}
