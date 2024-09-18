package com.ealas.restaurant_reservation_system.validation.user;

import com.ealas.restaurant_reservation_system.repository.IUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ExistsUsernameValidator implements ConstraintValidator<ExistsUsername, String> {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // Permite null si quieres manejar esto por separado
        }
        return !userRepository.existsByUsername(username);
    }
}
