package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tables")
@Getter @Setter
@ToString
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "table_number")
    private Integer tableNumber;

    @NotBlank
    @Size(max = 100)
    private String location;

    @NotBlank
    @Column(name = "number_seats")
    private Integer seats;

    @JsonIgnoreProperties({"tables", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnoreProperties({"tables", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationDetails> reservationDetails;

    public Mesa() {
        this.reservationDetails = new ArrayList<>();
    }
}
