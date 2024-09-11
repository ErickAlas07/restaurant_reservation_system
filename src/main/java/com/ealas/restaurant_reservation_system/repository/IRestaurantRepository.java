package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRestaurantRepository extends JpaRepository<Restaurant, Long> {

    boolean existsByName(String name);
}
