package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.User;

import java.util.List;

public interface UserService {
    User save(User user);

    List<User> findAll();

    boolean existsByUsername(String username);
}