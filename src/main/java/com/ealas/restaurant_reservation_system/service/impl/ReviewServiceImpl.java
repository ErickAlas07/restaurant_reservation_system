package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.entity.Review;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.IReviewRepository;
import com.ealas.restaurant_reservation_system.service.IReviewService;
import com.ealas.restaurant_reservation_system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    IReviewRepository reviewRepository;

    @Autowired
    IUserService userService;

    @Transactional(readOnly = true)
    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Transactional
    @Override
    public Review save(Review review) {
        //Obtener el nombre de usuario del usuario autenticado
        String username = userService.getCurrentUsername();

        // Buscamos usuario por username
        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            // Asociamos el usuario autenticado a la review
            review.setUser(user.get());
            review.setReviewDate(new java.util.Date());
            return reviewRepository.save(review);
        } else {
            throw new ResourceNotFoundException("User with username " + username + " not found.");
        }
    }

    @Transactional
    @Override
    public Optional<Review> delete(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            reviewRepository.delete(optionalReview.get());
            return optionalReview;
        } else {
            throw new ResourceNotFoundException("Review with id " + id + " not found.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Review> findReviewByUsername() {
        String username = userService.getCurrentUsername();
        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            return reviewRepository.findByUser(user.get());
        } else {
            throw new ResourceNotFoundException("User with username " + username + " not found.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Review> findHistoryByUserName(String username) {
        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            return reviewRepository.findHistoryByUserName(String.valueOf(user.get()));
        } else {
            throw new ResourceNotFoundException("User with username " + username + " not found.");
        }
    }
}
