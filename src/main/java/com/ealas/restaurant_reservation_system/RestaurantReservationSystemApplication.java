package com.ealas.restaurant_reservation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestaurantReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantReservationSystemApplication.class, args);
    }

}
