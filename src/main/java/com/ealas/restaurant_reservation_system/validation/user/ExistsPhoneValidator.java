package com.ealas.restaurant_reservation_system.validation.user;

import com.ealas.restaurant_reservation_system.repository.IUserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ExistsPhoneValidator implements ConstraintValidator<ExistsPhone, String> {

    @Autowired
    private IUserRepository userRepository;


    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return true;
        }
        return !userRepository.existsByPhone(phone);
    }
}
