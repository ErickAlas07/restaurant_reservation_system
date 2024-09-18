package com.ealas.restaurant_reservation_system.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsEmailValidator.class)
public @interface ExistsEmail {
    String message() default "Email is already taken";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
