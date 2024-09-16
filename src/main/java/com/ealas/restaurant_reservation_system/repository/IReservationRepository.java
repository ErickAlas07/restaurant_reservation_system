package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.reservationDate < :currentDate")
    List<Reservation> findPendingReservations(@Param("currentDate") LocalDateTime currentDate);
}
