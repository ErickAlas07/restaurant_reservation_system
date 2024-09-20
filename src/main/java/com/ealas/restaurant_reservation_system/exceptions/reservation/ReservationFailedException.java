package com.ealas.restaurant_reservation_system.exceptions.reservation;

public class ReservationFailedException extends RuntimeException {
    public ReservationFailedException(String message) {
        super(message);
    }
}
