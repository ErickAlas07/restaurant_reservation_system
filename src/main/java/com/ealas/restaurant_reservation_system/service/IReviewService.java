package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.Review;

import java.util.List;
import java.util.Optional;

public interface IReviewService {
    List<Review> findAll();

    Review save(Review review);

    Optional<Review> delete(Long id);

    List<Review> findReviewByUsername();

    List<Review> findHistoryByUserName(String username);
}
