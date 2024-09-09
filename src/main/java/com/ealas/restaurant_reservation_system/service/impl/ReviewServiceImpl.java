package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.entity.Review;
import com.ealas.restaurant_reservation_system.entity.User;
import com.ealas.restaurant_reservation_system.repository.IReviewRepository;
import com.ealas.restaurant_reservation_system.repository.IUserRepository;
import com.ealas.restaurant_reservation_system.service.IReviewService;
import com.ealas.restaurant_reservation_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    IReviewRepository reviewRepository;

    @Autowired
    UserService userService;

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
            throw new IllegalStateException("No se puede guardar la reseña, el usuario logueado no existe en la base de datos.");
        }
    }

    @Transactional
    @Override
    public Optional<Review> delete(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        optionalReview.ifPresent(reviewDb -> {
            reviewRepository.delete(reviewDb);
        });
        return optionalReview;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Review> findReviewByUsername() {
        String username = userService.getCurrentUsername();
        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            return reviewRepository.findByUser(user.get());
        } else {
            throw new IllegalStateException("El usuario logueado no existe en la base de datos.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Review> findHistoryByUserName(String username) {
        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            return reviewRepository.findByUser(user.get());
        } else {
            throw new IllegalStateException("El usuario logueado no existe en la base de datos.");
        }
    }
}
