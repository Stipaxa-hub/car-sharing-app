package org.app.carsharingapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.app.carsharingapp.validator.impl.PasswordValidator;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid format password. Password must contains"
            + " eight characters including one uppercase letter,"
            + " one lowercase letter, and one number or special character.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
