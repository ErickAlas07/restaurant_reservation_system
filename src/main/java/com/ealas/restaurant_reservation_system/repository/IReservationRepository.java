package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.reservationDate < :currentDate")
    List<Reservation> findPendingReservations(@Param("currentDate") LocalDate currentDate);

    int countByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByUserIdAndReservationDateAndReservationTime(Long userId, LocalDate reservationDate, LocalTime reservationTime);

    Optional<Reservation> findFirstByUserIdOrderByCreatedATDesc(Long userId);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.user.id = :userId " +
            "AND (r.reservationDate > :reservationDate " +
            "OR (r.reservationDate = :reservationDate AND r.reservationTime > :reservationTime))")
    int countByUserIdAndReservationDateTimeAfter(
            @Param("userId") Long userId,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("reservationTime") LocalTime reservationTime);


    List<Reservation> findAllByReservationDateBetween(LocalDate startDate, LocalDate endDate);
}
