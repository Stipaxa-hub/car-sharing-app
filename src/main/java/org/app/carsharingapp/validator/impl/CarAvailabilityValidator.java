package org.app.carsharingapp.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.app.carsharingapp.validator.CarAvailability;

public class CarAvailabilityValidator implements ConstraintValidator<CarAvailability, Integer> {

    @Override
    public boolean isValid(Integer inventory,
                           ConstraintValidatorContext constraintValidatorContext) {
        return inventory != null && inventory > 0;
    }
}
