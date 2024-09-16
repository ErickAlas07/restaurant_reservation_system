package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "total_amount")
    private Double totalAmount;

    @NotBlank
    @Column(name = "payment_method")
    private String paymentMethod;

    @NotNull
    @Column(name = "payment_date")
    private Date paymentDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(unique = true)
    private String uuid;

    @JsonIgnoreProperties({"payments", "handler", "hibernateLazyInitializer"})
    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @JsonIgnoreProperties({"payments", "handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
