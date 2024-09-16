package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.entity.Menu;
import com.ealas.restaurant_reservation_system.service.IMenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public List<Menu> list() {
        return menuService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable Long id) {
        Optional<Menu> optionalMenu = menuService.findById(id);
        if (optionalMenu.isPresent()) {
            return ResponseEntity.ok(optionalMenu.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Menu menu, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.save(menu));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Menu menu, BindingResult result, @PathVariable Long id) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.update(id, menu));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(menuService.delete(id));
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
