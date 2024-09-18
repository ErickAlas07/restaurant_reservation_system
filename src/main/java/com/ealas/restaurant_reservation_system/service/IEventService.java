package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;

import java.util.List;
import java.util.Optional;

public interface IEventService {
    List<EventDto> findAll();

    Optional<EventDto> findById(Long id);

    EventDto save(EventDto event);

    Optional<EventDto> update(Long id, EventDto event);

    Optional<EventDto> delete(Long id);
}
