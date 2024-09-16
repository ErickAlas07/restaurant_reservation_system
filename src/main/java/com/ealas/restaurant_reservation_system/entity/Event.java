package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "event_date")
    private Date eventDate;

    @NotNull
    @Column(name = "ticket_price")
    private Double ticketPrice;

    @NotNull
    private Integer capacity;

    @JsonIgnoreProperties({"events", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnoreProperties({"events", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    public Event() {
        this.reservations = new ArrayList<>();
    }
}
