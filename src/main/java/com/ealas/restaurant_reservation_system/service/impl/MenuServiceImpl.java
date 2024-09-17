package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.entity.Menu;
import com.ealas.restaurant_reservation_system.exceptions.ResourceAlreadyExistsException;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.IMenuRepository;
import com.ealas.restaurant_reservation_system.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements IMenuService {

    @Autowired
    IMenuRepository menuRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Menu> findById(Long id) {
        return Optional.ofNullable(menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu with id " + id + " not found.")));
    }

    @Transactional
    @Override
    public Menu save(Menu menu) {
        if (menuRepository.findByName(menu.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Menu with name: " + menu.getName() + " already exists.");
        }
        return menuRepository.save(menu);
    }

    @Transactional
    @Override
    public Optional<Menu> update(Long id, Menu menu) {
        Menu menuDb = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu with id " + id + " not found."));

        menuDb.setName(menu.getName());
        menuDb.setPrice(menu.getPrice());
        menuDb.setDescription(menu.getDescription());
        menuDb.setAvailable(menu.isAvailable());
        menuDb.setCategory(menu.getCategory());

        return Optional.of(menuRepository.save(menuDb));
    }

    @Transactional
    @Override
    public Optional<Menu> delete(Long id) {
        Menu menuDb = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu with id " + id + " not found."));

        menuRepository.delete(menuDb);
        return Optional.of(menuDb);
    }
}
