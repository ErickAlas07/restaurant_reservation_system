package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.user.UserDto;
import com.ealas.restaurant_reservation_system.dto.user.UserRegisterDto;
import com.ealas.restaurant_reservation_system.dto.user.UserUpdateDto;
import com.ealas.restaurant_reservation_system.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    UserRegisterDto save(UserRegisterDto user);

    List<UserDto> findAll();

    Optional<UserUpdateDto> update(Long id, UserUpdateDto userUpdateDto);

    String getCurrentUsername();

    Optional<User> findByUsername(String username);

    User authUsuario();

    UserDto getAuthenticatedUser();

    UserRegisterDto register (UserRegisterDto userRegisterDto);
}