package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);

    List<Payment> findByUserId(Long userId);
}
