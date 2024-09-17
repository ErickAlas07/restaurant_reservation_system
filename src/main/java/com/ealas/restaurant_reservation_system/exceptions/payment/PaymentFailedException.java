package com.ealas.restaurant_reservation_system.exceptions.payment;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
