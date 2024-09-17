package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.EventDto;
import com.ealas.restaurant_reservation_system.service.IEventService;
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
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
public class EventController {

    @Autowired
    private IEventService eventService;

    @Operation(summary = "List all events", description = "Returns a list of all events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<List<EventDto>> list() {
        List<EventDto> events = eventService.findAll();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Create a new event", description = "Creates a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Bad Syntax.")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventDto event, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(201).body(eventService.save(event));
    }

    @Operation(summary = "Update an event", description = "Updates an event based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Bad Syntax."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody EventDto event, @PathVariable Long id, BindingResult result) {
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
