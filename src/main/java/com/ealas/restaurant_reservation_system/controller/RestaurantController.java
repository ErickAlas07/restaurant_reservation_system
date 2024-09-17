package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.RestaurantDto;
import com.ealas.restaurant_reservation_system.service.IRestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/restaurants")
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
public class RestaurantController {

    @Autowired
    IRestaurantService restaurantService;

    @Operation(summary = "Get list of all restaurants", description = "Returns a list of all restaurants.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list."),
            @ApiResponse(responseCode = "404", description = "No restaurants found.")
    })
    @GetMapping
    public List<RestaurantDto> list() {
        return restaurantService.findAll();
    }

    @Operation(summary = "Get restaurant by ID", description = "Returns a restaurant based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved restaurant."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> view(@PathVariable Long id) {
        Optional<RestaurantDto> optionalRestaurant = restaurantService.findById(id);
        return optionalRestaurant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Register a new restaurant", description = "Creates a new restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created."),
            @ApiResponse(responseCode = "400", description = "Bad request.")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RestaurantDto restaurantDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(201).body(restaurantService.save(restaurantDto));
    }

    @Operation(summary = "Update an existing restaurant", description = "Updates an existing restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant updated."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody RestaurantDto restaurantDto, BindingResult result, @PathVariable Long id) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        Optional<RestaurantDto> updatedRestaurant = restaurantService.update(id, restaurantDto);
        return updatedRestaurant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a restaurant", description = "Deletes a restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant deleted."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<RestaurantDto> optionalRestaurant = restaurantService.delete(id);
        if (optionalRestaurant.isPresent()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
