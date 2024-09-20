package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.event.EventDto;
import com.ealas.restaurant_reservation_system.dto.reservation.*;
import com.ealas.restaurant_reservation_system.entity.*;
import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.ealas.restaurant_reservation_system.exceptions.ResourceNotFoundException;
import com.ealas.restaurant_reservation_system.exceptions.reservation.ReservationFailedException;
import com.ealas.restaurant_reservation_system.exceptions.table.TableFailedException;
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

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IEventRepository eventRepository;
    private final IMesaRepository mesaRepository;
    private final IUserRepository userRepository;
    private final IRestaurantRepository restaurantRepository;
    private final JavaMailSenderImpl mailSender;
    private final EmailService emailService;

    @Autowired
    public ReservationServiceImpl(IReservationRepository reservationRepository, IEventRepository eventRepository, IMesaRepository mesaRepository,
                                  IUserRepository userRepository, IRestaurantRepository restaurantRepository, JavaMailSenderImpl mailSender, EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.mesaRepository = mesaRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.mailSender = mailSender;
        this.emailService = emailService;
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado reservación con ID: " + id));
        return Optional.of(toDto(reservation));
    }

    @Transactional
    @Override
    public ReservationDto saveReservationTable(ReservationTableDto reservationDto) {
        Reservation reservation = toEntity(reservationDto);
        //Validación para que la fecha de la reserva no sea anterior a la fecha actual
        validateReservationDate(reservation.getReservationDate());
        // Validar el plazo de la reserva
        validateReservationTime(reservation);
        // Asignar el usuario y el restaurante por defecto
        setDefaultUserAndRestaurant(reservation);
        //Validación para el máximo de reservas permitidas por usuario
        validateRecentReservations(reservation.getUser().getId());
        validateTableReservation(reservation.getUser().getId(), reservation.getReservationDate(), reservation.getReservationTime());
        // Validar conflictos con eventos
        validateEventConflict(reservation.getReservationDate(), reservation.getReservationTime(), reservation.getRestaurant().getId());
        // Establecer el estado de la reserva si no se proporciona
        if (reservation.getStatus() == null) {
            reservation.setStatus(StatusReservation.PENDING);
        }
        reservation.setCreatedAT(LocalDateTime.now());

        // Asignar detalles de reserva
        assignReservationDetails(reservation);
        Reservation reservationDb = reservationRepository.save(reservation);

        // Enviar el correo de confirmación
        sendConfirmationEmail(reservationDb.getUser().getEmail(), reservationDb, "created");
        return toDto(reservationDb);
    }

    @Transactional
    @Override
    public ReservationEventDto saveReservationEvent(ReservationEventDto reservationDto) {
        Reservation reservation = toEntity(reservationDto);
        // Asignar el usuario y el restaurante por defecto
        setDefaultUserAndRestaurant(reservation);

        // Establecer el estado de la reserva si no se proporciona
        if (reservation.getStatus() == null) {
            reservation.setStatus(StatusReservation.PENDING);
        }
        // Asignar el evento si se proporciona en el DTO
        if (reservationDto.getEventId() != null) {
            Event event = eventRepository.findById(reservationDto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado el evento con ID: " + reservationDto.getEventId()));
            reservation.setEvent(event);
            event.setCapacity(event.getCapacity() - reservation.getPeople());
            // Asignar la fecha y la hora del evento a la reserva
            reservation.setReservationDate(event.getEventDate());
            reservation.setReservationTime(event.getStartTime());
        }
        reservation.setCreatedAT(LocalDateTime.now());

        //Validación para el máximo de reservas permitidas por usuario
        validateMaxEventReservations(reservation.getUser().getId(), reservationDto.getEventId());
        // Validar el plazo de la reserva
        validateReservationTime(reservation);
        // Asignar detalles de reserva
        assignReservationDetails(reservation);
        Reservation reservationDb = reservationRepository.save(reservation);

        // Enviar el correo de confirmación
        sendConfirmationEmail(reservationDb.getUser().getEmail(), reservationDb, "created");
        return toEventDto(reservationDb);
    }

    @Transactional
    @Override
    public Optional<ReservationDto> update(Long id, ReservationDto reservationDto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);

        if (reservationOptional.isPresent()) {
            Reservation reservationDb = reservationOptional.get();
            // Verificar si el estado de la reserva es "PENDIENTE"
            if (!reservationDb.getStatus().equals(StatusReservation.PENDING)) {
                throw new ReservationFailedException("Solo se pueden actualizar reservas con estado PENDIENTE.");
            }
            // Actualizar los campos de la reserva
            updateReservationFields(reservationDb, reservationDto);
            // Guardar los cambios
            Reservation reservationUpdated = reservationRepository.save(reservationDb);

            // Enviar correo electrónico al usuario
            sendConfirmationEmail(reservationUpdated.getUser().getEmail(), reservationUpdated, "updated");
            return Optional.of(toDto(reservationUpdated));
        } else {
            throw new ResourceNotFoundException("No se ha encontrado el evento con ID: " + id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ReservationDetailsDto getReservationDetails(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna reservación asociada con ID:  " + reservationId));

        return buildReservationDetailsDto(reservation);
    }

    @Override
    public List<ReservationDto> findReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.findAllByReservationDateBetween(startDate, endDate);
        return reservations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void validateReservationTime(Reservation reservation) {
        // Obtener la fecha y hora actual
        LocalDateTime currentDate = LocalDateTime.now(ZoneId.of("UTC"));

        // Combinar la fecha y la hora de la reserva en LocalDateTime
        LocalDate reservationDate = reservation.getReservationDate();
        LocalTime reservationTime = reservation.getReservationTime();
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);
        // Calcular la diferencia en horas
        long hoursDifference = Duration.between(currentDate, reservationDateTime).toHours();

        // Validar si la reserva es con al menos 24 horas de anticipación
        if (hoursDifference < 24) {
            throw new RuntimeException("Las reservas deben realizarse con al menos 24 horas de antelación.");
        }
    }

    private void setDefaultUserAndRestaurant(Reservation reservation) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con nombre de usuario: " + username));
        reservation.setUser(user);

        Restaurant restaurant = restaurantRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el restaurante predeterminado."));
        reservation.setRestaurant(restaurant);
    }

    private void updateReservationFields(Reservation reservationDb, ReservationDto reservationDto) {
        if (reservationDto.getReservationDate() != null)
            reservationDb.setReservationDate(reservationDto.getReservationDate());
        if (reservationDto.getReservationTime() != null)
            reservationDb.setReservationTime(reservationDto.getReservationTime());
        if (reservationDto.getOccasion() != null) reservationDb.setOccasion(reservationDto.getOccasion());
        if (reservationDto.getNotes() != null) reservationDb.setNotes(reservationDto.getNotes());
        if (reservationDto.getStatus() != null) reservationDb.setStatus(reservationDto.getStatus());
        if (reservationDto.getEventId() != null) {
            Event event = eventRepository.findById(reservationDto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado el evento: " + reservationDto.getEvent().getTitle()));
            reservationDb.setEvent(event);
        }
        if (reservationDto.getPeople() != null && reservationDto.getPeople().equals(reservationDb.getPeople())) {
            reservationDb.setPeople(reservationDto.getPeople());
            assignReservationDetails(reservationDb);
        }
    }

    private void assignReservationDetails(Reservation reservation) {
        Pageable pageable = PageRequest.of(0, 1);
        List<Mesa> tables = mesaRepository.findAvailableTable(reservation.getPeople(), pageable);
        if (tables.isEmpty()) {
            throw new TableFailedException("No hay mesas disponibles para " + reservation.getPeople() + " personas");
        }
        if (reservation.getReservationType() == ReservationType.TABLE) {
            List<ReservationDetails> details = new ArrayList<>();
            for (Mesa table : tables) {
                ReservationDetails detail = new ReservationDetails();
                detail.setReservationType(ReservationType.TABLE);
                detail.setPrice(calculatePriceForTable(table, reservation));
                detail.setReservation(reservation);
                detail.setTable(table);
                details.add(detail);
                table.setAvailable(false);
                mesaRepository.save(table);
            }
            reservation.setReservationDetails(details);

        } else if (reservation.getReservationType() == ReservationType.EVENT) {
            for (Mesa table : tables) {
                List<ReservationDetails> details = new ArrayList<>();
                ReservationDetails detail = new ReservationDetails();
                detail.setReservationType(ReservationType.EVENT);
                detail.setPrice(reservation.getEvent().getTicketPrice() * reservation.getPeople());
                detail.setReservation(reservation);
                detail.setTable(table);
                table.setAvailable(false);
                details.add(detail);
                reservation.setReservationDetails(details);
            }
        }
    }

    private double calculatePriceForTable(Mesa table, Reservation reservation) {
        double fixedPrice = 2.0;
        double extraPrice = 2.0;
        double OccasionPrice = 3.0;
        double totalPrice;
        totalPrice = fixedPrice + extraPrice + (reservation.getNotes() != null && reservation.getOccasion() != null ? OccasionPrice : 0);
        return totalPrice;
    }

    public void sendConfirmationEmail(String email, Reservation reservation, String action) {
        // Personaliza el asunto del correo según la acción
        String subject = action.equals("updated")
                ? "Tu reservación ha sido actualizada"
                : "Nueva reservación creada";

        // Construye el mensaje del correo según la acción
        String message = buildEmailMessage(reservation, action);
        // Configuración del correo
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);
        mailSender.send(emailMessage);
    }

    private String buildEmailMessage(Reservation reservation, String action) {
        // Construye el mensaje según si la acción es "created" o "updated"
        String message;
        if (action.equals("updated")) {
            // Mensaje personalizado para actualización de reserva
            message = "Estimado/a " + reservation.getUser().getUsername() + ",\n\n";

            if (reservation.getEvent() != null) {
                message += "Has confirmado una reservación para el evento: \n\n" + reservation.getEvent().getTitle() + "\n" +
                        "Fecha del evento: " + reservation.getEvent().getEventDate() + "\n" +
                        "Hora de inicio: " + reservation.getEvent().getStartTime() + "\n" +
                        "Hora de fin: " + reservation.getEvent().getEndTime() + "\n";
                message += "Precio total: $" + reservation.getReservationDetails().stream()
                        .mapToDouble(ReservationDetails::getPrice)
                        .sum() + "\n";
                message += "Estado de la reserva: " + reservation.getStatus() + "\n";
                message += "Te recordamos que debes pagar tu reserva antes del " + reservation.getReservationDate() + "\n\n";
            } else {
                message += "Has confirmado una reservación para una mesa en nuestro restaurante.\n\n";
                message += "Fecha de Reservación: " + reservation.getReservationDate() + "\n" +
                        "Hora de Reservación: " + reservation.getReservationTime() + "\n" +
                        "Reservación para: " + reservation.getPeople() + " personas\n";
                if (reservation.getOccasion() != null)
                    message += "Ocasión Especial: " + reservation.getOccasion() + "\n";
                if (reservation.getNotes() != null) message += "Notas extras: " + reservation.getNotes() + "\n";
                message += "Mesa asignada: " + reservation.getReservationDetails().get(0).getTable().getTableNumber() + "\n";
            }
            message += "Estado de reserva: " + reservation.getStatus() + "\n";
            message += "Precio total: $" + reservation.getReservationDetails().stream()
                    .mapToDouble(ReservationDetails::getPrice)
                    .sum() + "\n\n";
            message += "Te recordamos que debes pagar tu reserva antes del " + reservation.getReservationDate() + "\n\n";
        } else {
            // Mensaje personalizado para nueva reserva
            message = "Estimado/a " + reservation.getUser().getUsername() + ",\n\n" +
                    "Gracias por realizar una reserva en nuestro restaurante.\n" +
                    "A continuación te mostramos los detalles de tu reserva:\n\n";
            if (reservation.getEvent() != null) {
                message += "Detalles del evento: \n\n" + reservation.getEvent().getTitle() + "\n" +
                        "Fecha del evento: " + reservation.getEvent().getEventDate() + "\n" +
                        "Hora de inicio: " + reservation.getEvent().getStartTime() + "\n" +
                        "Hora de fin: " + reservation.getEvent().getEndTime() + "\n";
                message += "Precio total: $" + reservation.getReservationDetails().stream()
                        .mapToDouble(ReservationDetails::getPrice)
                        .sum() + "\n";
            } else {
                message += "Fecha de Reservación: " + reservation.getReservationDate() + "\n" +
                        "Hora de Reservación: " + reservation.getReservationTime() + "\n" +
                        "Número de personas: " + reservation.getPeople() + "\n";
                if (reservation.getOccasion() != null)
                    message += "Ocasión Especial: " + reservation.getOccasion() + "\n";
                if (reservation.getNotes() != null) message += "Notas extras: " + reservation.getNotes() + "\n";
                message += "Precio total: $" + reservation.getReservationDetails().stream()
                        .mapToDouble(ReservationDetails::getPrice)
                        .sum() + "\n";
                message += "Estado de reserva: " + reservation.getStatus() + "\n";
                message += "Te recordamos que debes confirmar tu reserva antes de " + reservation.getReservationDate() +
                        ", de lo contrario será cancelada automáticamente.\n\n";
            }
        }
        message += "¡Gracias por elegirnos!\n\nAtentamente,\n" +
                "El equipo de " + reservation.getRestaurant().getName();
        return message;
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    private void validateEventConflict(LocalDate reservationDate, LocalTime reservationTime, Long restaurantId) {
        // Obtener los eventos en la fecha y restaurante especificados
        List<Event> activeEvents = eventRepository.findByRestaurantIdAndDateAndTime(restaurantId, reservationDate, reservationTime);
        // Si existe algún evento en conflicto
        if (!activeEvents.isEmpty()) {
            Event conflictEvent = activeEvents.get(0);
            throw new ReservationFailedException("No se pueden realizar reservas de mesas durante el evento: " + conflictEvent.getTitle());
        }
    }

    private void validateReservationDate(LocalDate reservationDate) {
        LocalDate today = LocalDate.now();
        if (reservationDate.isBefore(today)) {
            throw new ReservationFailedException("No está permitido hacer reservaciones con fechas anteriores.");
        }
    }

    public void validateTableReservation(Long userId, LocalDate reservationDate, LocalTime reservationTime) {
        // Verificar si ya existe una reserva para la misma fecha y hora
        boolean duplicateReservation = reservationRepository.existsByUserIdAndReservationDateAndReservationTime(userId, reservationDate, reservationTime);
        if (duplicateReservation) {
            throw new ReservationFailedException("Ya tienes una reserva para esa fecha y hora.");
        }
        // Validar máximo de 3 reservas en las últimas 24 horas
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDate currentDate = last24Hours.toLocalDate();
        LocalTime currentTime = last24Hours.toLocalTime();
        int count24HourReservations = reservationRepository.countByUserIdAndReservationDateTimeAfter(userId, currentDate, currentTime);
        if (count24HourReservations >= 3) {
            throw new ReservationFailedException("Has alcanzado el máximo de 3 reservas en las últimas 24 horas.");
        }
    }

    public void validateRecentReservations(Long userId) {
        Optional<Reservation> lastReservationOpt = reservationRepository.findFirstByUserIdOrderByCreatedATDesc(userId);
        if (lastReservationOpt.isPresent()) {
            Reservation lastReservation = lastReservationOpt.get();
            // Obtener el timestamp de la última reserva
            LocalDateTime lastReservationCreatedAt = lastReservation.getCreatedAT();
            // Verificar si han pasado al menos 5 horas desde la última reserva
            if (lastReservationCreatedAt.isAfter(LocalDateTime.now().minusHours(5))) {
                throw new ReservationFailedException("Solo puedes hacer otra reserva después de 5 horas de la última reserva.");
            }
        }
    }

    public void validateMaxEventReservations(Long userId, Long eventId) {
        int count = reservationRepository.countByUserIdAndEventId(userId, eventId);
        if (count >= 2) {
            throw new ReservationFailedException("No puedes reservar más de 2 veces para el mismo evento.");
        }
    }

    @Override
    public double calculateTotalRevenue(List<ReservationDto> reservations) {
        double totalRevenue = 0;

        // Filtra solo las reservas confirmadas
        List<ReservationDto> confirmedReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == StatusReservation.CONFIRMED)
                .collect(Collectors.toList());

        // Calcular las ganancias de mesas
        double totalByTables = confirmedReservations.stream()
                .flatMap(reservation -> {
                    List<ReservationDetailsDto> details = reservation.getReservationDetails();
                    return (details != null) ? details.stream() : Stream.empty();
                })
                .filter(detail -> detail.getReservationType() == ReservationType.TABLE)
                .mapToDouble(ReservationDetailsDto::getTotalPrice)
                .sum();

        // Calcular las ganancias de eventos
        double totalByEvents = confirmedReservations.stream()
                .flatMap(reservation -> {
                    List<ReservationDetailsDto> details = reservation.getReservationDetails();
                    return (details != null) ? details.stream() : Stream.empty();
                })
                .filter(detail -> detail.getReservationType() == ReservationType.EVENT)
                .mapToDouble(ReservationDetailsDto::getTotalPrice)
                .sum();

        totalRevenue = totalByTables + totalByEvents;

        return totalRevenue;
    }

    @Override
    public ReservationDto cancelReservation(Long id) {
        // Obtener la reservación por ID
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservación no encontrada."));

        // Obtener la fecha y hora actual
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/El_Salvador"));

        // Combinar la fecha y hora de la reservación
        LocalDateTime reservationDateTime = LocalDateTime.of(reservation.getReservationDate(), reservation.getReservationTime());

        // Calcular la diferencia en horas
        long hoursUntilReservation = java.time.Duration.between(now, reservationDateTime).toHours();

        // Validar si faltan más de 3 horas para la reservación
        if (hoursUntilReservation < 3) {
            throw new RuntimeException("La reserva no se puede cancelar con menos de 3 horas de antelación.");
        }

        // Actualizar el estado de la reserva a 'CANCELLED'
        reservation.setStatus(StatusReservation.CANCELED);
        reservationRepository.save(reservation);

        // Si la reservación tiene una mesa asignada, liberarla
        reservation.getReservationDetails().forEach(detail -> {
            Mesa mesa = detail.getTable();
            if (mesa != null && mesa.isAvailable()) {
                mesa.setAvailable(true);
                mesaRepository.save(mesa);
            }
        });

        System.out.println("La reserva con ID " + id + " ha sido cancelada y la mesa liberada.");

        emailService.sendCancellationEmail(reservation.getUser(), reservation);
        return toDto(reservation);
    }


    private ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        BeanUtils.copyProperties(reservation, dto);

        // Mapear el evento si existe
        if (reservation.getEvent() != null) {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(reservation.getEvent(), eventDto);
            dto.setEvent(eventDto);  // Asignar el evento al DTO
        }

        // Calcular el total basado en los detalles de la reserva
        dto.setTotalAmount(reservation.getReservationDetails().stream()
                .mapToDouble(ReservationDetails::getPrice) // Solo usamos el precio
                .sum());


        return dto;
    }

    private Reservation toEntity(ReservationTableDto reservationDto) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDto, reservation);
        return reservation;
    }

    private Reservation toEntity(ReservationEventDto reservationDto) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDto, reservation);
        // El evento se maneja después en el servicio, por eso no se setea aquí
        return reservation;
    }

    private ReservationDetailsDto buildReservationDetailsDto(Reservation reservation) {
        ReservationDetailsDto dto = new ReservationDetailsDto();

        dto.setReservationId(reservation.getId());
        dto.setReservationDate(String.valueOf(reservation.getReservationDate()));
        dto.setReservationTime(String.valueOf(reservation.getReservationTime()));

        Optional.ofNullable(reservation.getPeople()).ifPresent(dto::setPeople);
        Optional.ofNullable(reservation.getOccasion()).ifPresent(o -> dto.setOccasion(String.valueOf(o)));
        Optional.ofNullable(reservation.getNotes()).ifPresent(dto::setNotes);

        dto.setStatus(String.valueOf(reservation.getStatus()));

        // Calcular el total del precio de los detalles de la reserva
        dto.setTotalPrice(reservation.getReservationDetails().stream()
                .mapToDouble(ReservationDetails::getPrice)
                .sum());

        // Mapear el evento si existe
        Optional.ofNullable(reservation.getEvent()).ifPresent(event -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(event, eventDto);
            dto.setEvent(eventDto);
        });
        return dto;
    }

    public ReservationEventDto toEventDto(Reservation reservation) {
        ReservationEventDto dto = new ReservationEventDto();
        BeanUtils.copyProperties(reservation, dto);

        // Mapear el evento si existe
        Optional.ofNullable(reservation.getEvent()).ifPresent(event -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(event, eventDto);
            dto.setEvent(eventDto);  // Asignar el evento al DTO
        });
        return dto;
    }
}