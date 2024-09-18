package com.ealas.restaurant_reservation_system.validation.user;

import com.ealas.restaurant_reservation_system.repository.IUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ExistsEmailValidator implements ConstraintValidator<ExistsEmail, String> {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }
        return !userRepository.existsByEmail(email);
    }
}
