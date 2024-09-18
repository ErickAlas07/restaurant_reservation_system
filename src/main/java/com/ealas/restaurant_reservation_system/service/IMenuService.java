package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.menu.MenuDto;
import com.ealas.restaurant_reservation_system.entity.Menu;

import java.util.List;
import java.util.Optional;

public interface IMenuService {
    List<MenuDto> findAll();

    Optional<MenuDto> findById(Long id);

    MenuDto save (MenuDto menu);

    Optional<MenuDto> update(Long id, MenuDto menuDto);

    List<MenuDto> findAllAvailable();
}
