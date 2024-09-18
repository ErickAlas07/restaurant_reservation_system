package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.user.UserDto;
import com.ealas.restaurant_reservation_system.dto.user.UserRegisterDto;
import com.ealas.restaurant_reservation_system.dto.user.UserUpdateDto;
import com.ealas.restaurant_reservation_system.entity.Role;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.IRoleRepository;
import com.ealas.restaurant_reservation_system.repository.IUserRepository;
import com.ealas.restaurant_reservation_system.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserRegisterDto save(UserRegisterDto userRegisterDto) {
        User userEntity = new User();

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        optionalRoleUser.ifPresent(roles::add);

        if (userRegisterDto.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(roles::add);
        }

        userEntity.setRoles(roles);
        userEntity.setCreatedAt(new Date());

        // Encriptar la contraseña
        String encodedPassword = passwordEncoder.encode(userRegisterDto.getPassword());
        userEntity.setPassword(encodedPassword);

        // Copiar otras propiedades del DTO a la entidad
        BeanUtils.copyProperties(userRegisterDto, userEntity, "password", "roles");

        User savedUser = userRepository.save(userEntity);
        return mapUserToRegisterDto(savedUser);
    }

    @Transactional
    @Override
    public UserRegisterDto register(UserRegisterDto userRegisterDto) {
        return save(userRegisterDto);
    }

    @Transactional
    @Override
    public Optional<UserUpdateDto> update(Long id, UserUpdateDto userUpdateDto) {
            Optional<User> optionalUser = userRepository.findById(id);
            if(optionalUser.isPresent()){
                User userDb = optionalUser.get();
                if(userUpdateDto.getName() != null){
                    userDb.setName(userUpdateDto.getName());
                }
                if(userUpdateDto.getLastname() != null){
                    userDb.setLastname(userUpdateDto.getLastname());
                }
                if(userUpdateDto.getEmail() != null){
                    userDb.setEmail(userUpdateDto.getEmail());
                }
                if(userUpdateDto.getPhone() != null){
                    userDb.setPhone(userUpdateDto.getPhone());
                }
                if(userUpdateDto.getAddress() != null){
                    userDb.setAddress(userUpdateDto.getAddress());
                }
                if(userUpdateDto.getGender() != null){
                    userDb.setGender(userUpdateDto.getGender());
                }

                User userUpdate = userRepository.save(userDb);

                return Optional.of(mapUserToUpdateDto(userUpdate));
            } else{
                throw new ResourceNotFoundException("User not found with ID: " + id);
            }
    }

    @Override
    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User authUsuario() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getAuthenticatedUser() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // Conversión de User a UserDto directamente en el servicio
        return mapUserToDto(user);
    }

    private UserDto mapUserToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }

    private UserUpdateDto mapUserToUpdateDto(User user) {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        BeanUtils.copyProperties(user, userUpdateDto);

        return userUpdateDto;
    }

    private UserRegisterDto mapUserToRegisterDto(User user) {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        BeanUtils.copyProperties(user, userRegisterDto);
        return userRegisterDto;
    }
}