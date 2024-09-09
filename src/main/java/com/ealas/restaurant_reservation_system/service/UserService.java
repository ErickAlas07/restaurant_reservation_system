package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.UserUpdateDto;
import com.ealas.restaurant_reservation_system.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);

    List<User> findAll();

    Optional<User> update(Long id, UserUpdateDto userUpdateDto);

    public String getCurrentUsername();

    public Optional<User> findByUsername(String username);

    public User authUsuario();
}