package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.entity.Menu;
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
        return (List<Menu>) menuRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }

    @Transactional
    @Override
    public Menu save(Menu menu) {
        if (menuRepository.findByName(menu.getName()).isPresent()) {
            throw new RuntimeException("Menu already exists");
        }
        return menuRepository.save(menu);
    }

    @Transactional
    @Override
    public Optional<Menu> update(Long id, Menu menu) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if(optionalMenu.isPresent()){
            Menu menudb = optionalMenu.orElseThrow();

            menudb.setName(menu.getName());
            menudb.setPrice(menu.getPrice());
            menudb.setDescription(menu.getDescription());
            menudb.setAvailable(menu.isAvailable());
            menudb.setCategory(menu.getCategory());
            return Optional.of(menuRepository.save(menudb));
        }

        return optionalMenu;
    }

    @Transactional
    @Override
    public Optional<Menu> delete(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        optionalMenu.ifPresent(menuDb -> {
            menuRepository.delete(menuDb);
        });
        return optionalMenu;
    }
}
