package com.ealas.restaurant_reservation_system.exceptions.table;

public class TableFailedException extends RuntimeException{
    public TableFailedException(String message) {
        super(message);
    }
}
