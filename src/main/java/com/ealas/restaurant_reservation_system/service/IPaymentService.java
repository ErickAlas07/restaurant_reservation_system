package com.ealas.restaurant_reservation_system.service;

import com.ealas.restaurant_reservation_system.dto.PaymentDto;
import com.ealas.restaurant_reservation_system.enums.PaymentMethod;

import java.util.List;

public interface IPaymentService {
    PaymentDto createPayment(PaymentDto paymentDto);

    List<PaymentDto> findPaymentsByUserId(Long userId);
}
