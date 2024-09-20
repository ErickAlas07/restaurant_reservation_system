package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tables")
@Data
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "table_number")
    private Integer tableNumber;

    @NotBlank
    @Size(max = 100)
    private String location;

    @NotNull
    @Column(name = "number_seats")
    private Integer seats;

    @NotNull
    @Column(name = "available")
    private boolean available;

    @JsonIgnoreProperties({"tables", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnoreProperties({"tables", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationDetails> reservationDetails;

    public Mesa() {
        this.reservationDetails = new ArrayList<>();
    }
}
