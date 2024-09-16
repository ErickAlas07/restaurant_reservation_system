package com.ealas.restaurant_reservation_system.entity;

import com.ealas.restaurant_reservation_system.enums.ReservationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "reservationdetails")
@Getter
@Setter
@ToString
public class ReservationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_type")
    private ReservationType reservationType;

    @NotNull
    private Double price;

    @JsonIgnoreProperties({"reservationDetails", "handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @JsonIgnoreProperties({"reservationDetails", "handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private Mesa table;
}
