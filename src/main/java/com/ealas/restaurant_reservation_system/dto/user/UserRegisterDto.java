package com.ealas.restaurant_reservation_system.dto.user;

import com.ealas.restaurant_reservation_system.entity.Role;
import com.ealas.restaurant_reservation_system.validation.user.ExistsEmail;
import com.ealas.restaurant_reservation_system.validation.user.ExistsUsername;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

    @NotBlank
    @Size(min = 4, max = 12)
    @ExistsUsername
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank
    @Email
    @ExistsEmail
    private String email;

    private boolean admin;
}
