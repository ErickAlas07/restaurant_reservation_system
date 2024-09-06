package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.Menu;

import java.util.List;
import java.util.Optional;

public interface IMenuService {
    List<Menu> findAll();

    Optional<Menu> findById(Long id);

    Menu save (Menu menu);

    Optional<Menu> update(Long id, Menu menu);

    Optional<Menu> delete(Long id);
}
