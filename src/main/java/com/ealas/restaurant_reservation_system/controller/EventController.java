package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.EventDto;
import com.ealas.restaurant_reservation_system.service.IEventService;
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
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
public class EventController {

    @Autowired
    private IEventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> list() {
        List<EventDto> events = eventService.findAll();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventDto event, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(201).body(eventService.save(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody  EventDto event, @PathVariable Long id, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        Optional<EventDto> updatedEvent = eventService.update(id, event);
        return updatedEvent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
