package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Event;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface IEventRepository extends JpaRepository<Event, Long> {
   boolean existsByTitle(String title);

   @Query("SELECT e FROM Event e WHERE e.restaurant.id = :restaurantId " +
           "AND e.eventDate = :date " +
           "AND (:reservationTime = e.startTime OR :reservationTime = e.endTime OR :reservationTime BETWEEN e.startTime AND e.endTime)")
   List<Event> findByRestaurantIdAndDateAndTime(
           @Param("restaurantId") Long restaurantId,
           @Param("date") LocalDate date,
           @Param("reservationTime") LocalTime reservationTime);
}
