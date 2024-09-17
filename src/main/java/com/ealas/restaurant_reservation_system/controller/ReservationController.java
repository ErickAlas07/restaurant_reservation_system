package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.ReservationDto;
import com.ealas.restaurant_reservation_system.dto.ReservationDetailsDto;
import com.ealas.restaurant_reservation_system.entity.Reservation;
import com.ealas.restaurant_reservation_system.service.IReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private IReservationService reservationService;

    @Operation(summary = "Get list of all reservations", description = "Returns a list of all reservations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reservations."),
            @ApiResponse(responseCode = "404", description = "No reservations found.")
    })
    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return new ResponseEntity<>(reservationService.findAll(), HttpStatus.OK);
    }

    @Operation(summary = "Get reservation by ID", description = "Returns a reservation based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reservation."),
            @ApiResponse(responseCode = "404", description = "Reservation not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable("id") Long id) {
        Optional<ReservationDto> reservation = reservationService.findById(id);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new reservation", description = "Creates a new reservation and returns the created reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Validation errors occurred.")
    })
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDto reservationDto, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        ReservationDto newReservation = reservationService.save(reservationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReservation);
    }

    @Operation(summary = "Update an existing reservation", description = "Updates a reservation by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Validation errors occurred."),
            @ApiResponse(responseCode = "404", description = "Reservation not found.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable("id") Long id, @RequestBody ReservationDto reservationDto) {
        Optional<ReservationDto> updatedReservation = reservationService.update(id, reservationDto);
        return updatedReservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get reservation details", description = "Returns details of a reservation including additional details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reservation details."),
            @ApiResponse(responseCode = "404", description = "Reservation not found.")
    })
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getReservationDetails(@PathVariable Long id) {
        ReservationDetailsDto reservationDetails = reservationService.getReservationDetails(id);
        return ResponseEntity.ok(reservationDetails);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
