package com.ealas.restaurant_reservation_system.controller;

import com.ealas.restaurant_reservation_system.entity.Review;
import com.ealas.restaurant_reservation_system.service.IReviewService;
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

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    @Operation(summary = "List all reviews", description = "Returns a list of all reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping
    public List<Review> list() {
        return reviewService.findAll();
    }

    @Operation(summary = "Get review by ID", description = "Returns a review based on their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request. Validation errors occurred.")
    })
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Review review, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        Review savedReview = reviewService.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @Operation(summary = "Delete a review", description = "Deletes a review by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully."),
            @ApiResponse(responseCode = "404", description = "Review not found.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get reviews by authenticated user", description = "Returns a list of reviews made by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's reviews."),
            @ApiResponse(responseCode = "404", description = "No reviews found for the authenticated user.")
    })
    @GetMapping("/user")
    public ResponseEntity<List<Review>> listOwReviews() {
        List<Review> reviews = reviewService.findReviewByUsername();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get review history by username", description = "Returns the review history for a specific user by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's review history."),
            @ApiResponse(responseCode = "404", description = "No reviews found for the specified user.")
    })
    @GetMapping("/history/{username}")
    public ResponseEntity<List<Review>> listHistoryByUsername(@PathVariable String username) {
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
