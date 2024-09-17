package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.ReservationDto;
import com.ealas.restaurant_reservation_system.dto.ReservationDetailsDto;
import com.ealas.restaurant_reservation_system.entity.Reservation;
import com.ealas.restaurant_reservation_system.service.IReservationService;
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

    // Obtener todas las reservas
    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return new ResponseEntity<>(reservationService.findAll(), HttpStatus.OK);
    }

    // Obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable("id") Long id) {
        Optional<ReservationDto> reservation = reservationService.findById(id);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDto reservationDto, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        ReservationDto newReservation = reservationService.save(reservationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReservation);
    }

    // Actualizar una reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable("id") Long id, @RequestBody ReservationDto reservationDto) {
        Optional<ReservationDto> updatedReservation = reservationService.update(id, reservationDto);
        return updatedReservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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
