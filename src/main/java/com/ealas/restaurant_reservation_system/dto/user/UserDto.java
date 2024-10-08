package com.ealas.restaurant_reservation_system.dto.user;

import com.ealas.restaurant_reservation_system.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String username;

    private String name;

    private String lastname;

    private String email;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
