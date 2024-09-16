package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {
}
