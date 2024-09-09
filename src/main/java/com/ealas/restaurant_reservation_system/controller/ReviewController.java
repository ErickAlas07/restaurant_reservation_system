package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.entity.Review;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.service.IReviewService;
import com.ealas.restaurant_reservation_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    @GetMapping
    public List<Review> list(){
        return reviewService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Review review, BindingResult result){
        if (result.hasErrors()) {
            return validation(result);
        }
        Review savedReview = reviewService.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<Review>> listOwReviews(){
        List<Review> reviews = reviewService.findReviewByUsername();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<Review>> listHistoryByUsername(@PathVariable String username){
        List<Review> reviews = reviewService.findHistoryByUserName(username);
        return ResponseEntity.ok(reviews);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "The field " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
