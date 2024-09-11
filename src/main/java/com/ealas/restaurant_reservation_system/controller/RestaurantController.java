package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.RestaurantDto;
import com.ealas.restaurant_reservation_system.service.IRestaurantService;
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

    @GetMapping
    public List<RestaurantDto> list() {
        return restaurantService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> view(@PathVariable Long id) {
        Optional<RestaurantDto> optionalRestaurant = restaurantService.findById(id);
        return optionalRestaurant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RestaurantDto restaurantDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(201).body(restaurantService.save(restaurantDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody RestaurantDto restaurantDto, BindingResult result, @PathVariable Long id) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        Optional<RestaurantDto> updatedRestaurant = restaurantService.update(id, restaurantDto);
        return updatedRestaurant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
