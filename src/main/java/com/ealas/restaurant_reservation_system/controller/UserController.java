package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.UserRegisterDto;
import com.ealas.restaurant_reservation_system.dto.UserUpdateDto;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> autenticarUsuario() {
        User actualUsuario = userService.authUsuario();  // Obtener usuario autenticado
        return ResponseEntity.ok(actualUsuario);  // Devolver datos del usuario autenticado
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto userRegisterDTO, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(userRegisterDTO.getPassword());
        user.setEmail(userRegisterDTO.getEmail());
        user.setAdmin(false);
        user.setCreatedAt(new Date());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateDto userUpdateDto, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        User currentUser = userService.authUsuario();
        Optional<User> updatedUser = userService.update(currentUser.getId(), userUpdateDto);

        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser.get());
        }
        return ResponseEntity.notFound().build();
    }


    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "The field " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
