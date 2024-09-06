package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {
    List<Promotion> findAll();

    Optional<Promotion> findById(Long id);

    Promotion save(Promotion promotion);

    Optional<Promotion> update(Long id, Promotion promotion);

    Optional<Promotion> delete(Long id);

    Optional<Promotion> findByTitle(String title);
}
