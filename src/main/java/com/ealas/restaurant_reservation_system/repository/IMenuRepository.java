package com.ealas.restaurant_reservation_system.repository;

import com.ealas.restaurant_reservation_system.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByName(String name);

    List<Menu> findByAvailableTrue();
}
