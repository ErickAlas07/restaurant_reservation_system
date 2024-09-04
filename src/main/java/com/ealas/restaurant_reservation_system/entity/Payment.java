package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "total_amount")
    private Double totalAmount;

    @NotBlank
    @Column(name = "payment_method")
    private String paymentMethod;

    @NotBlank
    @Column(name = "payment_date")
    private LocalDate paymentDate;

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
