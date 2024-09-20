package com.ealas.restaurant_reservation_system.entity;

import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAT;

    @Column(name = "reservation_date")
    private LocalDate reservationDate;

    @Column(name = "reservation_time")
    private LocalTime reservationTime;

    @NotNull
    @Column(name = "number_of_people")
    private Integer people;

    @Enumerated(EnumType.STRING)
    private StatusReservation status;

    @Enumerated(EnumType.STRING)
    private SpecialOccasion occasion;

    @Column(name = "special_requests")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_type")
    private ReservationType reservationType;

    @JsonIgnoreProperties({"reservations", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnoreProperties({"reservations", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnoreProperties({"reservations", "handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @JsonIgnoreProperties({"reservations", "handler", "hibernateLazyInitializer"})
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private Payment payment;

    @JsonIgnoreProperties({"reservation", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationDetails> reservationDetails;

    public Reservation() {
        this.reservationDetails = new ArrayList<>();
    }
}
