package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import com.ealas.restaurant_reservation_system.entity.Event;
import com.ealas.restaurant_reservation_system.exceptions.ResourceAlreadyExistsException;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
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
        return Optional.ofNullable(eventRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found.")));
    }

    @Transactional
    @Override
    public EventDto save(EventDto eventDto) {
        boolean exists = eventRepository.existsByTitle(eventDto.getTitle());
        if (!exists) {
            Event eventEntity = toEntity(eventDto);
            Event eventDb = eventRepository.save(eventEntity);
            return toDTO(eventDb);
        } else {
            throw new ResourceAlreadyExistsException("Event with title " + eventDto.getTitle() + " already exists.");
        }
    }

    @Transactional
    @Override
    public Optional<EventDto> update(Long id, EventDto eventDto) {
        Event eventDb = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found."));

        // Actualizar los campos de la entidad Event
        if (eventDto.getTitle() != null) eventDb.setTitle(eventDto.getTitle());
        if (eventDto.getDescription() != null) eventDb.setDescription(eventDto.getDescription());
        if (eventDto.getEventDate() != null) eventDb.setEventDate(eventDto.getEventDate());
        if (eventDto.getTicketPrice() != null) eventDb.setTicketPrice(eventDto.getTicketPrice());
        if (eventDto.getCapacity() != null) eventDb.setCapacity(eventDto.getCapacity());
        if(eventDto.getStartTime() != null) eventDb.setStartTime(eventDto.getStartTime());
        if(eventDto.getEndTime() != null) eventDb.setEndTime(eventDto.getEndTime());

        Event eventUpdated = eventRepository.save(eventDb);
        return Optional.of(toDTO(eventUpdated));
    }

    @Transactional
    @Override
    public Optional<EventDto> delete(Long id) {
        Event eventDb = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found."));

        eventRepository.delete(eventDb);
        return Optional.of(toDTO(eventDb));
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
        event.setStartTime(eventDto.getStartTime());
        event.setEndTime(eventDto.getEndTime());
        return event;
    }
}
