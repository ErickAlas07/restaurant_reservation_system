package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String comment;

    @NotBlank
    private Integer rating;

    @NotBlank
    @Column(name = "review_date")
    private Date date;

    @JsonIgnoreProperties({"reviews", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnoreProperties({"reviews", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
