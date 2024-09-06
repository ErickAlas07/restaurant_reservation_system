package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.entity.Promotion;
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
        return promotionRepository.findById(id);
    }

    @Transactional
    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Transactional
    @Override
    public Optional<Promotion> update(Long id, Promotion promotion) {
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);
        if(optionalPromotion.isPresent()){
            Promotion promotiondb = optionalPromotion.orElseThrow();

            promotiondb.setTitle(promotion.getTitle());
            promotiondb.setDescription(promotion.getDescription());
            promotiondb.setConditions(promotion.getConditions());
            promotiondb.setStartDate(promotion.getStartDate());
            promotiondb.setEndDate(promotion.getEndDate());

            return Optional.of(promotionRepository.save(promotiondb));
        }

        return optionalPromotion;
    }

    @Transactional
    @Override
    public Optional<Promotion> delete(Long id) {
        Optional<Promotion> optionalPromotion = promotionRepository.findById(id);
        optionalPromotion.ifPresent(promotionDb -> {
            promotionRepository.delete(promotionDb);
        });
        return optionalPromotion;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Promotion> findByTitle(String title) {
        return promotionRepository.findByTitle(title);
    }
}
