package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.entity.Promotion;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.IPromotionRepository;
import com.ealas.restaurant_reservation_system.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements IPromotionService {

    @Autowired
    IPromotionRepository promotionRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Promotion> findAll() {
        return (List<Promotion>) promotionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Promotion> findById(Long id) {
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);
        if(optionalPromotion.isEmpty()){
            throw new ResourceNotFoundException("Promotion not found with id " + id);
        }
        return optionalPromotion;
    }

    @Transactional
    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Transactional
    @Override
    public Optional<Promotion> update(Long id, Promotion promotion) {
        Promotion promotionDb = promotionRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No se ha encontrado promoción con ID: " + id));

        // Actualizar los campos de la promoción
        promotionDb.setTitle(promotion.getTitle());
        promotionDb.setDescription(promotion.getDescription());
        promotionDb.setConditions(promotion.getConditions());
        promotionDb.setStartDate(promotion.getStartDate());
        promotionDb.setEndDate(promotion.getEndDate());

        return Optional.of(promotionRepository.save(promotionDb));
    }

    @Transactional
    @Override
    public Optional<Promotion> delete(Long id) {
        Optional<Promotion> optionalPromotion = Optional.of(promotionRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No se ha encontrado promoción con ID: " + id)));
        optionalPromotion.ifPresent(promotionDb -> {
            promotionRepository.delete(promotionDb);
        });
        return optionalPromotion;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Promotion> findByTitle(String title) {
        return Optional.ofNullable(promotionRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción: '" + title + "' no encontrada.")));
    }
}
