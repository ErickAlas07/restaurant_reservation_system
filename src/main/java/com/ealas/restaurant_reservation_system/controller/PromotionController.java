package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.entity.Promotion;
import com.ealas.restaurant_reservation_system.service.IPromotionService;
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
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
public class PromotionController {

    @Autowired
    IPromotionService promotionService;

    @Operation(summary = "List all promotions", description = "Returns a list of all promotions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public List<Promotion> list() {
        return promotionService.findAll();
    }

    @Operation(summary = "Get promotion by ID", description = "Returns a promotion based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable Long id) {
        Optional<Promotion> optionalPromotion = promotionService.findById(id);
        if (optionalPromotion.isPresent()) {
            return ResponseEntity.ok(optionalPromotion.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Register a new promotion", description = "Returns the promotion created")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Bad Syntax.")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Promotion promotion, BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.save(promotion));
    }

    @Operation(summary = "Update a promotion", description = "Returns the promotion updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Bad Syntax."),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Promotion promotion, BindingResult result, @PathVariable Long id) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        Optional<Promotion> optionalPromotion = promotionService.update(id, promotion);
        if (optionalPromotion.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalPromotion.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a promotion", description = "Returns the promotion deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Promotion> optionalPromotion = promotionService.delete(id);
        if (optionalPromotion.isPresent()) {
            return ResponseEntity.ok().build();
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
