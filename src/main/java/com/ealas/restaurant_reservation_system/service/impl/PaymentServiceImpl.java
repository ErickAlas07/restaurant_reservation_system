package com.ealas.restaurant_reservation_system.service.impl;

import com.ealas.restaurant_reservation_system.dto.PaymentDto;
import com.ealas.restaurant_reservation_system.dto.ReservationDto;
import com.ealas.restaurant_reservation_system.entity.Payment;
import com.ealas.restaurant_reservation_system.entity.Reservation;
import com.ealas.restaurant_reservation_system.entity.ReservationDetails;
import com.ealas.restaurant_reservation_system.enums.PaymentStatus;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.ealas.restaurant_reservation_system.repository.IPaymentRepository;
import com.ealas.restaurant_reservation_system.repository.IReservationRepository;
import com.ealas.restaurant_reservation_system.service.EmailService;
import com.ealas.restaurant_reservation_system.service.IPaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements IPaymentService {
    private final IPaymentRepository paymentRepository;
    private final IReservationRepository reservationRepository;
    private final EmailService emailService;

    @Autowired
    public PaymentServiceImpl(IPaymentRepository paymentRepository, IReservationRepository reservationRepository, EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public PaymentDto createPayment(PaymentDto paymentDto) {
        // Buscar la reservación a partir del ID de la reservación en el DTO
        Reservation reservation = reservationRepository.findById(paymentDto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + paymentDto.getReservationId()));

        // Verificar si ya existe un pago para esta reservación
        paymentRepository.findByReservationId(reservation.getId()).ifPresent(payment -> {
            throw new RuntimeException("Payment already exists for reservation id " + reservation.getId());
        });

        List<ReservationDetails> reservationDetails = reservation.getReservationDetails();

        if(reservation.getStatus() != StatusReservation.CONFIRMED) {
            throw new RuntimeException("Reservation is not confirmed");
        }

        // Crear el pago y asignar los valores desde el DTO
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setTotalAmount(reservationDetails.stream().mapToDouble(ReservationDetails::getPrice).sum());
        payment.setUuid(UUID.randomUUID().toString()); // Generar UUID aleatorio
        payment.setReservation(reservation);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setUser(reservation.getUser());
        // Guardar el pago en la base de datos
        Payment savedPayment = paymentRepository.save(payment);

        // Enviar un email al usuario confirmando el pago
        emailService.sendPaymentConfirmationEmail(reservation.getUser(), savedPayment);
        // Retornar el PaymentDto como respuesta
        return toDto(savedPayment);

    }

    @Override
    public List<PaymentDto> findPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(this::toDto).toList();
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        BeanUtils.copyProperties(payment, paymentDto);

        // Convertir el objeto Reservation a un ReservationDto
        ReservationDto reservationDto = new ReservationDto();
        Reservation reservation = payment.getReservation();
        BeanUtils.copyProperties(reservation, reservationDto);

        paymentDto.setReservationId(reservation.getId());
        paymentDto.setUserId(reservation.getUser().getId());
        paymentDto.setReservation(reservationDto);
        return paymentDto;
    }

}
