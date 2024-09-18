package com.ealas.restaurant_reservation_system.dto;

import com.ealas.restaurant_reservation_system.dto.reservation.ReservationDto;
import com.ealas.restaurant_reservation_system.enums.PaymentMethod;
import com.ealas.restaurant_reservation_system.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Double totalAmount;
    private PaymentMethod paymentMethod;
    private Date paymentDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long reservationId;

    private Long userId;
    private PaymentStatus paymentStatus;
    private String uuid;
    private ReservationDto reservation;
}
