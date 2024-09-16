package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Mesa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMesaRepository extends JpaRepository<Mesa, Long> {

    @Query("SELECT m FROM Mesa m WHERE m.available = true AND m.seats >= :people")
    List<Mesa> findAvailableTable(int people, Pageable pageable);

    Boolean existsByTableNumber(Integer tableNumber);
}
