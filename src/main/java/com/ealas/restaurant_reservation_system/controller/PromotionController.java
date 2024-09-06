package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.entity.Promotion;
import com.ealas.restaurant_reservation_system.service.IPromotionService;
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
public class PromotionController {

    @Autowired
    IPromotionService promotionService;

    @GetMapping
    public List<Promotion> list(){
        return promotionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable Long id) {
        Optional<Promotion> optionalPromotion = promotionService.findById(id);
        if(optionalPromotion.isPresent()){
            return ResponseEntity.ok(optionalPromotion.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Promotion promotion, BindingResult result){
        if(result.hasFieldErrors()){
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.save(promotion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Promotion promotion, BindingResult result, @PathVariable Long id){
        if(result.hasFieldErrors()){
            return validation(result);
        }

        Optional<Promotion> optionalPromotion = promotionService.update(id, promotion);
        if(optionalPromotion.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalPromotion.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<Promotion> optionalPromotion = promotionService.delete(id);
        if(optionalPromotion.isPresent()){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result){
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "The field " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
