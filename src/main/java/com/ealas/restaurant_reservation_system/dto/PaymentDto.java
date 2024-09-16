package com.ealas.restaurant_reservation_system.dto;

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
    private Long id;
    private Double totalAmount;
    private String paymentMethod;
    private Date paymentDate;
    private String uuid;
    private Long reservationId;
    private Long userId;
}
