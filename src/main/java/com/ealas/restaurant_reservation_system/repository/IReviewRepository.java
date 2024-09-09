package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Review;
import com.ealas.restaurant_reservation_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUser(User user);

    @Query("SELECT r FROM Review r WHERE r.user.username = ?1")
    List<Review> findHistoryByUserName(String username);
}
