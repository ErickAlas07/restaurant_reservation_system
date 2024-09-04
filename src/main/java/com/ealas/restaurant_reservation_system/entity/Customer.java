package com.ealas.restaurant_reservation_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer extends User {

    @NotBlank
    private String phone;

    @NotBlank
    private LocalDate birthdate;

    @Column(unique = true)
    @NotBlank
    @Size(max = 9)
    private String dui;

    private String address;

    public Customer() {
        super();
    }
}