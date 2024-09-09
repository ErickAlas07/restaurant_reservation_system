package com.ealas.restaurant_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String comment;

    @NotNull
    private Integer rating;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "review_date")
    private Date reviewDate;

    @JsonIgnoreProperties({"reviews", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnoreProperties({"reviews", "handler", "hibernateLazyInitializer"})
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
