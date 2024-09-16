package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.EventDto;
import com.ealas.restaurant_reservation_system.entity.Event;
import com.ealas.restaurant_reservation_system.repository.IEventRepository;
import com.ealas.restaurant_reservation_system.service.IEventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements IEventService {

    @Autowired
    IEventRepository eventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> findAll() {
        return eventRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EventDto> findById(Long id) {
        return Optional.empty();
    }

    @Transactional
    @Override
    public EventDto save(EventDto eventDto) {
        boolean exists = eventRepository.existsByTitle(eventDto.getTitle());
        if(!exists) {
            Event eventEntity = toEntity(eventDto);
            Event eventDb = eventRepository.save(eventEntity);
            return toDTO(eventDb);
        } else {
            throw new RuntimeException("Event already exists");
        }
    }

    @Transactional
    @Override
    public Optional<EventDto> update(Long id, EventDto event) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {
            Event eventDb = eventOptional.get();
            if (event.getTitle() != null) eventDb.setTitle(event.getTitle());
            if (event.getDescription() != null) eventDb.setDescription(event.getDescription());
            if (event.getEventDate() != null) eventDb.setEventDate(event.getEventDate());
            if (event.getTicketPrice() != null) eventDb.setTicketPrice(event.getTicketPrice());
            if (event.getCapacity() != null) eventDb.setCapacity(event.getCapacity());

            Event eventUpdated = eventRepository.save(eventDb);
            return Optional.of(toDTO(eventUpdated));
        }

        return Optional.empty();
    }

    @Transactional
    @Override
    public Optional<EventDto> delete(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if(event.isPresent()){
            eventRepository.delete(event.get());
            return Optional.of(toDTO(event.get()));
        }
        return Optional.empty();
    }

    public EventDto toDTO(Event event) {
        EventDto dto = new EventDto();
        BeanUtils.copyProperties(event, dto);
        return dto;
    }

    public static Event toEntity(EventDto eventDto) {
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setTicketPrice(eventDto.getTicketPrice());
        event.setCapacity(eventDto.getCapacity());
        return event;
    }
}
