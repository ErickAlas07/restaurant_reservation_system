package com.ealas.restaurant_reservation_system.entity;

import com.ealas.restaurant_reservation_system.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public  class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  /*  @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 12)*/
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

//    @NotBlank
//    @Email
    @Column(unique = true)
    private String email;

    private String name;

    private String lastname;

    private String phone;

    private String address;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "last_login")
    private Date lastLogin;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private boolean enabled;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    private List<Role> roles = new ArrayList<>();

    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations = new ArrayList<>();

    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
    @OneToMany(mappedBy = "user")
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.enabled = true;
    }
}
