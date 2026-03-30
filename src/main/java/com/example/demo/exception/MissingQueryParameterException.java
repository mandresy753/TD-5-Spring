package com.example.demo.exception;

public class MissingQueryParameterException extends RuntimeException {
    public MissingQueryParameterException(String message) {
        super(message);
    }
}
