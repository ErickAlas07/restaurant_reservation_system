package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.user.*;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
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

    @Operation(summary = "Get list of all users", description = "Returns a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list."),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource."),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden."),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found.")
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        return new ResponseEntity<>(userService.findAll(),HttpStatus.OK);
    }

    @Operation(summary = "Get user by ID", description = "Get authenticated user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved authenticated user."),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> autenticarUsuario() {
        UserDto userDto = userService.getAuthenticatedUser();
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Register a new user", description = "Register a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto userRegisterDTO, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        UserRegisterDto user = userService.register(userRegisterDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Update user", description = "Update user details (profile).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request.")
    })
    @PutMapping("/update/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateDto userUpdateDto, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        User currentUser = userService.authUsuario();
        Optional<UserUpdateDto> updatedUser = userService.update(currentUser.getId(), userUpdateDto);

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