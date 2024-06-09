package org.app.carsharingapp.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.app.carsharingapp.validator.FieldMatcher;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatcherValidator implements ConstraintValidator<FieldMatcher, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatcher constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value)
                .getPropertyValue(fieldMatch);

        if (fieldValue != null) {
            return fieldValue.equals(fieldMatchValue);
        } else {
            return fieldMatchValue == null;
        }
    }
}
