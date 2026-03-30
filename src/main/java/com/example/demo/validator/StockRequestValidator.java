package com.example.demo.validator;

import com.example.demo.enums.UnitType;
import com.example.demo.exception.MissingQueryParameterException;
import org.springframework.stereotype.Component;

@Component
public class StockRequestValidator {

    public void validate(String at, UnitType unit) {
        if (at == null || at.isEmpty() || unit == null) {
            throw new MissingQueryParameterException(
                    "Either mandatory query parameter `at` or `unit` is not provided."
            );
        }
    }
}