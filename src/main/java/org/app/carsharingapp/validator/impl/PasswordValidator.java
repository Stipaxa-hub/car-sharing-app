package org.app.carsharingapp.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.app.carsharingapp.validator.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String REGEX_FOR_PASSWORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_=+-]).{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && Pattern.compile(REGEX_FOR_PASSWORD).matcher(password).matches();
    }
}
