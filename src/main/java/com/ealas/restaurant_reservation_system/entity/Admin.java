package com.ealas.restaurant_reservation_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@Setter
@ToString
public class Admin extends User {

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    private String department;

    @Transient
    private boolean admin;

    public Admin() {
        super();
    }
}