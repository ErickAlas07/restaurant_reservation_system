package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "menus")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name_menu")
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private Double price;

    @NotBlank
    private boolean available;

    @NotBlank
    @Size(max = 60)
    private String category;

    @JsonIgnoreProperties({"menus", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
