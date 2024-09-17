package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.EventDto;
import com.ealas.restaurant_reservation_system.dto.ReservationDetailsDto;
import com.ealas.restaurant_reservation_system.dto.ReservationDto;
import com.ealas.restaurant_reservation_system.dto.ReservationTableDto;
import com.ealas.restaurant_reservation_system.entity.*;
import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.repository.*;
import com.ealas.restaurant_reservation_system.service.EmailService;
import com.ealas.restaurant_reservation_system.service.IReservationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements IReservationService {

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IMesaRepository mesaRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Transactional(readOnly = true)
    @Override
    public List<ReservationDto> findAll() {
        return reservationRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ReservationDto> findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + id));
        return Optional.of(toDto(reservation));
    }

    @Transactional
    @Override
    public ReservationDto save(ReservationDto reservationDto) {
        Reservation reservation = toEntity(reservationDto);

        //Lógica para validar el pllazo de la reserva
        LocalDateTime currentDate = LocalDateTime.now(ZoneId.of("UTC"));

        // Convertir la fecha de la reserva (Date) a LocalDateTime
        LocalDateTime reservationDate = reservation.getReservationDate()
                .toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();

        // Validar si la reserva es con al menos 24 horas de anticipación
        long hoursDifference = Duration.between(currentDate, reservationDate).toHours();
        if (hoursDifference < 24) {
            throw new RuntimeException("Reservations must be made at least 24 hours in advance.");
        }

        //Lógica para asignar el usuario y el restaurante por defecto
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));
        reservation.setUser(user);

        Restaurant restaurant = restaurantRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Default restaurant not found"));
        reservation.setRestaurant(restaurant);

        // Establecer el estado de la reserva si no se proporciona
        if (reservation.getStatus() == null) {
            reservation.setStatus(StatusReservation.PENDING);
        }

        // Asignar el evento si se proporciona
        if (reservationDto.getEventId() != null) {
            Event event = eventRepository.findById(reservationDto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + reservationDto.getEventId()));
            reservation.setEvent(event);
        }
        assignReservationDetails(reservation);

        Reservation reservationDb = reservationRepository.save(reservation);

        // Enviar el correo de recordatorio
        String subject = "Nueva reserva en el restaurante";
        String text = "Estimado/a " + user.getUsername() + ",\n\n" +
                "Gracias por realizar una reserva en nuestro restaurante. " +
                "Te recordamos que debes confirmar tu reserva antes de " + reservation.getReservationDate() +
                ", de lo contrario será cancelada automáticamente.\n\n" +
                "Detalles de tu reserva:\n" +
                "Fecha y hora: " + reservation.getReservationDate() + "\n" +
                "Número de personas: " + reservation.getPeople() + "\n" +
                "Notas: " + reservation.getNotes() + "\n\n" +
                "¡Esperamos verte pronto!\n" +
                "El equipo del restaurante";

        emailService.sendReservationReminder(user.getEmail(), subject, text);

        return toDto(reservationDb);
    }

    @Transactional
    @Override
    public Optional<ReservationDto> update(Long id, ReservationDto reservationDto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isPresent()) {
            Reservation reservationDb = reservationOptional.get();
            if (reservationDto.getReservationDate() != null)
                reservationDb.setReservationDate(reservationDto.getReservationDate());
            if (reservationDto.getOccasion() != null) reservationDb.setOccasion(reservationDto.getOccasion());
            if (reservationDto.getNotes() != null) reservationDb.setNotes(reservationDto.getNotes());
            if (reservationDto.getStatus() != null) reservationDb.setStatus(reservationDto.getStatus());
            if (reservationDto.getEventId() != null) {
                Event event = eventRepository.findById(reservationDto.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + reservationDto.getEventId()));
                reservationDb.setEvent(event);
            }

            if (reservationDto.getPeople() != null && reservationDto.getPeople().equals(reservationDb.getPeople())) {
                reservationDb.setPeople(reservationDto.getPeople());
                assignReservationDetails(reservationDb);
            }
            Reservation reservationUpdated = reservationRepository.save(reservationDb);

            if (reservationUpdated.getStatus().equals(StatusReservation.CONFIRMED)) {
                // Enviar correo electrónico al usuario
                sendConfirmationEmail(reservationDb.getUser().getEmail(), reservationUpdated, "confirmed");
            } else {
                // Enviar correo electrónico al usuario
                sendConfirmationEmail(reservationDb.getUser().getEmail(), reservationUpdated, "updated");
            }

            return Optional.of(toDto(reservationUpdated));
        } else {
            throw new RuntimeException("Reservation not found with id " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDetailsDto getReservationDetails(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id " + reservationId));

        ReservationDetailsDto responseDto = new ReservationDetailsDto();
        responseDto.setReservationId(reservation.getId());
        responseDto.setReservationDate(reservation.getReservationDate().toString());
        responseDto.setOccasion(String.valueOf(reservation.getOccasion()));
        responseDto.setPeople(reservation.getPeople());
        responseDto.setNotes(reservation.getNotes());

        responseDto.setStatus(String.valueOf(reservation.getStatus()));

        List<ReservationTableDto> tableDtos = reservation.getReservationDetails().stream()
                .map(detail -> {
                    ReservationTableDto tableDto = new ReservationTableDto();
                    tableDto.setTableId(detail.getTable().getId());
                    tableDto.setTableNumber(detail.getTable().getTableNumber());
                    tableDto.setLocation(detail.getTable().getLocation());
                    return tableDto;
                }).collect(Collectors.toList());

        responseDto.setTables(tableDtos);

        if (reservation.getEvent() != null) {
            EventDto eventDto = new EventDto();
            eventDto.setTitle(reservation.getEvent().getTitle());
            eventDto.setDescription(reservation.getEvent().getDescription());
            eventDto.setTicketPrice(reservation.getEvent().getTicketPrice());
            eventDto.setEventDate(reservation.getEvent().getEventDate());

            responseDto.setEvent(eventDto);
        }

        responseDto.setTotalPrice(reservation.getReservationDetails().stream()
                .mapToDouble(ReservationDetails::getPrice)
                .sum());

        return responseDto;
    }

    //Métodos extras para la funcionalidad de la reserva
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    private void assignReservationDetails(Reservation reservation) {
        Pageable pageable = PageRequest.of(0, 1); // Limit to 1 result
        List<Mesa> tables = mesaRepository.findAvailableTable(reservation.getPeople(), pageable);
        if (tables.isEmpty()) {
            throw new RuntimeException("No available tables for " + reservation.getPeople() + " people");
        }

        List<ReservationDetails> details = new ArrayList<>();
        for (Mesa table : tables) {
            ReservationDetails detail = new ReservationDetails();
            // Establecer el tipo de reserva basado en la presencia de un evento
            if (reservation.getEvent() != null) {
                detail.setReservationType(ReservationType.EVENT);
                detail.setPrice(reservation.getEvent().getTicketPrice() * reservation.getPeople());
            } else {
                detail.setReservationType(ReservationType.TABLE);
                detail.setPrice(calculatePriceForTable(table, reservation));
            }

            detail.setReservation(reservation);
            detail.setTable(table);
            details.add(detail);

            table.setAvailable(false);
            mesaRepository.save(table);
        }

        reservation.setReservationDetails(details);
    }

    private double calculatePriceForTable(Mesa table, Reservation reservation) {
        double fixedPrice = 2.0;
        double extraPrice = 2.0;
        double OccasionPrice = 3.0;
        double totalPrice;
        if (reservation.getNotes() != null && reservation.getOccasion() != null) {
            totalPrice = fixedPrice + extraPrice + OccasionPrice;
        } else {
            totalPrice = fixedPrice + extraPrice;
        }
        return totalPrice;
    }

    public void sendConfirmationEmail(String email, Reservation reservation, String action) {
        String subject = action.equals("updated") ? "Tu reservación ha sido actualizada" : "Tu reservación ha sido confirmada";
        String message = buildEmailMessage(reservation, action);

        // Configuración del correo
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);
        mailSender.send(emailMessage);
    }

    private String buildEmailMessage(Reservation reservation, String action) {
        String message = action.equals("updated")
                ? "Tu reservación ha sido actualizada con los siguientes detalles:\n\n"
                : "Tu reservación ha sido confirmada con los siguientes detalles:\n\n";

        message += "Fecha de Reservación: " + reservation.getReservationDate() + "\n";
        message += "Número de personas " + reservation.getPeople() + "\n";
        message += "Ocasión Especial: " + reservation.getOccasion() + "\n";
        message += "Notas extras: " + reservation.getNotes() + "\n";
        message += reservation.getEvent() != null ? "Evento: " + reservation.getEvent().getTitle() + "\n" : "";
        message += "Estado de reserva: " + reservation.getStatus() + "\n";
        message += "Precio total: $" + reservation.getReservationDetails().stream()
                .mapToDouble(ReservationDetails::getPrice)
                .sum() + "\n\n";

        return message;
    }

    private ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        BeanUtils.copyProperties(reservation, dto);

        // Asignar el eventId si la reserva tiene un evento asociado
        if (reservation.getEvent() != null) {
            dto.setEventId(reservation.getEvent().getId());

            EventDto eventDto = new EventDto();
            dto.setEvent(eventDto);
        }

        return dto;
    }

    private Reservation toEntity(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDto, reservation);

        if (reservationDto.getEventId() != null) {
            Event event = new Event();
            event.setId(reservationDto.getEventId());
            reservation.setEvent(event);
        }
        return reservation;
    }
}
