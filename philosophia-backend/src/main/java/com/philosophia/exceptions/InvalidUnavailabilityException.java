package com.philosophia.exceptions;

public class InvalidUnavailabilityException extends RuntimeException {
    public InvalidUnavailabilityException(String message) {
        super(message);
    }
}