package com.example.driver_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class DriverStatusValidator implements ConstraintValidator<DriverStatusValue, String> {

    private final List<String> allowedValues = Arrays.asList("AVAILABLE", "OFFLINE");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // null 값은 허용하지 않음
        }
        return allowedValues.contains(value.toUpperCase());
    }
}