package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.MesaDto;
import com.ealas.restaurant_reservation_system.entity.Mesa;
import com.ealas.restaurant_reservation_system.service.IMesaService;
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
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
public class MesaController {

    @Autowired
    private IMesaService mesaService;

    @GetMapping
    public ResponseEntity< List<MesaDto>> findAll() {
        List<MesaDto> mesas = mesaService.findAll();
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDto> findById(@PathVariable(name = "id") Long id) {
        Optional<MesaDto> mesa = mesaService.findById(id);
        return mesa.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MesaDto mesaDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        MesaDto mesaBD = mesaService.save(mesaDto);
        return ResponseEntity.status(201).body(mesaBD);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @Valid @RequestBody MesaDto mesaDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        Optional<MesaDto> mesaBD = mesaService.update(id, mesaDto);
        return mesaBD.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Mesa> mesaOptional = mesaService.delete(id);

        if (mesaOptional.isPresent()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
