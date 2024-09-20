package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Data

public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "restaurant_name")
    private String name;

    private String description;

    @NotBlank
    private String address;

    @NotBlank
    @Size(max = 12)
    private String phone;

    @Email
    private String email;

    @NotBlank

    @Size(max = 50)
    private String city;

    @NotNull
    private Integer capacity;

    @NotBlank
    @Column(name = "opening_hours")
    private String openingHours;

    @Size(max = 50)
    private String websiteUrl;

    @JsonIgnoreProperties({"restaurant", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "restaurant")
    private List<Promotion> promotions;

    @JsonIgnoreProperties({"restaurant", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "restaurant")
    private List<Menu> menus;

    @JsonIgnoreProperties({"restaurant", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;

    @JsonIgnoreProperties({"restaurant", "handler", "hibernateLazyInitializer"})
    @ToString.Exclude
    @OneToMany(mappedBy = "restaurant")
    private List<Mesa> tables;

    @JsonIgnoreProperties({"restaurant", "handler", "hibernateLazyInitializer"})
    @ToString.Exclude
    @OneToMany(mappedBy = "restaurant")
    private List<Event> events;

    public Restaurant() {
        this.promotions = new ArrayList<>();
        this.menus = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.events = new ArrayList<>();
    }
}
