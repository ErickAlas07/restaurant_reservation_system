package com.ealas.restaurant_reservation_system.entity;

import com.ealas.restaurant_reservation_system.enums.SpecialOccasion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@ToString
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "reservation_date")
    private Date reservationDate;

    @NotBlank
    @Column(name = "number_of_people")
    private Integer people;

    @NotBlank
    @Size(max = 25)
    private String status;

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
