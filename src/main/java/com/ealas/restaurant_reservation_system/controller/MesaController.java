package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.dto.table.MesaDto;
import com.ealas.restaurant_reservation_system.service.IMesaService;
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
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
public class MesaController {

    @Autowired
    private IMesaService mesaService;

    @Operation(summary = "List all tables", description = "Returns a list of all tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden. You don't have permission to access this resource"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<List<MesaDto>> findAll() {
        List<MesaDto> mesas = mesaService.findAll();
        return ResponseEntity.ok(mesas);
    }

    @Operation(summary = "Get table by ID", description = "Returns a table based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Forbidden. You don't have permission to access this resource"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MesaDto> findById(@PathVariable(name = "id") Long id) {
        Optional<MesaDto> mesa = mesaService.findById(id);
        return mesa.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new table", description = "Creates a new table")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "403", description = "Forbidden. You don't have permission to access this resource"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Bad Syntax.")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MesaDto mesaDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        MesaDto mesaBD = mesaService.save(mesaDto);
        return ResponseEntity.status(201).body(mesaBD);
    }

    @Operation(summary = "Update a table", description = "Updates a table based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Bad Syntax."),
            @ApiResponse(responseCode = "403", description = "Forbidden. You don't have permission to access this resource"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @Valid @RequestBody MesaDto mesaDto, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }
        Optional<MesaDto> mesaBD = mesaService.update(id, mesaDto);
        return mesaBD.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> errors.put(err.getField(), "The field " + err.getField() + " " + err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
}
