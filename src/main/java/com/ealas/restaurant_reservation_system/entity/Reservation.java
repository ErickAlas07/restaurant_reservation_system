package com.ealas.restaurant_reservation_system.entity;

import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.ealas.restaurant_reservation_system.enums.StatusReservation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "reservation_date")
    private Date reservationDate;

    @NotNull
    @Column(name = "number_of_people")
    private Integer people;

    @Enumerated(EnumType.STRING)
    private StatusReservation status;

    @Enumerated(EnumType.STRING)
    private SpecialOccasion occasion;

    @Column(name = "special_requests")
    private String notes;

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
